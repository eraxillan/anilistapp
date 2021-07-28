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

package name.eraxillan.anilistapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import name.eraxillan.anilistapp.db.MediaDatabase
import name.eraxillan.anilistapp.utilities.ANIME_DATA_FILENAME
import name.eraxillan.anilistapp.utilities.mediaListFromJson
import timber.log.Timber


class MediaDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        // NOTE: see https://developer.android.com/topic/libraries/architecture/workmanager/advanced/coroutineworker
        withContext(Dispatchers.IO) {
            val result = kotlin.runCatching {
                applicationContext.assets.open(ANIME_DATA_FILENAME).use { inputStream ->
                    val mediaList = mediaListFromJson(inputStream.reader())
                    Timber.e("Mock media list loaded: ${mediaList.size}")

                    val database = MediaDatabase.getInstance(applicationContext)
                    database.mediaDao().insertMediaList(mediaList)
                    Result.success()
                }
            }.onFailure {
                Timber.e(it, "Unable to add mock media list to database")
                Result.failure()
            }

            result.getOrNull() ?: Result.failure()
        }
    }
}
