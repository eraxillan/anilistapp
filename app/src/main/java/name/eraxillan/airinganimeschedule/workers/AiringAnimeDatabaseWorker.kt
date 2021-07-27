/*
 * Copyright 2021 Aleksandr Kamyshnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package name.eraxillan.airinganimeschedule.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import name.eraxillan.airinganimeschedule.db.MediaDatabase
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

                    val database = MediaDatabase.getInstance(applicationContext)
                    database.mediaDao().insertMediaList(animeList)
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
