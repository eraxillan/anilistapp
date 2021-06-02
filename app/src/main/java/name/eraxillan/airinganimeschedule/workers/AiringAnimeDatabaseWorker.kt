package name.eraxillan.airinganimeschedule.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import name.eraxillan.airinganimeschedule.db.AiringAnimeDatabase
import name.eraxillan.airinganimeschedule.utilities.ANIME_DATA_FILENAME
import name.eraxillan.airinganimeschedule.utilities.airingAnimeListFromJson


class AiringAnimeDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        // NOTE: see https://developer.android.com/topic/libraries/architecture/workmanager/advanced/coroutineworker
        withContext(Dispatchers.IO) {

            val result = kotlin.runCatching {
                applicationContext.assets.open(ANIME_DATA_FILENAME).use { inputStream ->
                    val animeList = airingAnimeListFromJson(inputStream.reader())
                    Log.e(LOG_TAG, "Mock airing anime list loaded: ${animeList.size}")

                    val database = AiringAnimeDatabase.getInstance(applicationContext)
                    database.airingAnimeDao().insertAll(animeList)
                    Result.success()
                }
            }.onFailure {
                Log.e(LOG_TAG, "Unable to add mock airing anime list to database", it)
                Result.failure()
            }

            result.getOrNull() ?: Result.failure()
        }
    }

    companion object {
        // 54BE6C87 = crc32("name.eraxillan.airinganimeschedule")
        private const val LOG_TAG = "54BE6C87_DBW" // DBW = DatabaseWorker
    }
}
