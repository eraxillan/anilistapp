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
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.*
import name.eraxillan.anilistapp.data.room.MediaDatabase
import name.eraxillan.anilistapp.utilities.*
import timber.log.Timber


class MediaDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private lateinit var database: MediaDatabase

    override suspend fun doWork(): Result = coroutineScope {
        database = MediaDatabase.getInstance(applicationContext)

        // NOTE: see https://developer.android.com/topic/libraries/architecture/workmanager/advanced/coroutineworker
        // `runCatching` used just to suppress "Inappropriate blocking call" false positive
        withContext(Dispatchers.IO) {
            val result = kotlin.runCatching {
                ///fillMediaData()
                fillMediaTables()

                Result.success()
            }.onFailure {
                Timber.e(it, "Unable to add mock media list to database")
                Result.failure()
            }

            result.getOrNull() ?: Result.failure()
        }
    }

    /*private suspend fun fillMediaData() {
        // Load test media list from resources and append to database
        kotlin.runCatching {
            applicationContext.assets.open(MEDIA_DATA_FILENAME).use { inputStream ->
                val mediaList = mediaListFromJson(inputStream.reader())
                Timber.d("Mock media list loaded: ${mediaList.size}")

                mediaList
            }
        }.onSuccess { mediaList ->
            database.mediaDao().insertAll(mediaList.map { convertRemoteMediaToLocal(it) })
        }.onFailure {
            throw Exception("fillMediaData failed")
        }
    }*/

    private suspend fun fillMediaTables() {
        val firstUpdate = workDataOf(INIT_DATABASE_WORKER_PROGRESS_KEY to 0)
        val lastUpdate = workDataOf(INIT_DATABASE_WORKER_PROGRESS_KEY to 100)

        Timber.d("Filling media predefined tables...")
        setProgress(firstUpdate)

        // TODO: update info from backend first

        database.withTransaction {
            // Load media genre list from resources and append it to database
            kotlin.runCatching {
                applicationContext.assets.open(GENRE_DATA_FILENAME).use { inputStream ->
                    val mediaGenreList = mediaGenreListFromJson(inputStream.reader())
                    check(mediaGenreList.isNotEmpty())

                    Timber.d("Predefined media genre list loaded: ${mediaGenreList.size} elements")
                    mediaGenreList
                }
            }.onSuccess { mediaGenreList ->
                check(database.mediaGenreDao().getCount() == 0L)
                database.mediaGenreDao().insertAll(mediaGenreList)
                check(database.mediaGenreDao().getCount() == mediaGenreList.size.toLong())

                Timber.d("${mediaGenreList.size} media genres added to database")
            }.onFailure {
                throw Exception("fillMediaTables failed: genre list not filled!")
            }

            // Load media tag list from resources and append it to database
            kotlin.runCatching {
                applicationContext.assets.open(TAG_DATA_FILENAME).use { inputStream ->
                    val mediaTagList = mediaTagListFromJson(inputStream.reader())
                    check(mediaTagList.isNotEmpty())

                    Timber.d("Predefined media tag list loaded: ${mediaTagList.size} elements")
                    mediaTagList
                }
            }.onSuccess { mediaTagList ->
                check(database.mediaTagDao().getCount() == 0L)
                database.mediaTagDao().insertAll(mediaTagList)
                check(database.mediaTagDao().getCount() == mediaTagList.size.toLong())

                Timber.d("${mediaTagList.size} media tags added to database")
            }.onFailure {
                throw Exception("fillMediaTables failed: tag list not filled!")
            }

            // Load media studio list from resources and append it to database
            kotlin.runCatching {
                applicationContext.assets.open(STUDIO_DATA_FILENAME).use { inputStream ->
                    val mediaStudioList = mediaStudioListFromJson(inputStream.reader())
                    check(mediaStudioList.isNotEmpty())

                    Timber.d("Predefined media studio list loaded: ${mediaStudioList.size} elements")
                    mediaStudioList
                }
            }.onSuccess { mediaStudioList ->
                check(database.mediaStudioDao().getCount() == 0L)
                database.mediaStudioDao().insertAll(mediaStudioList)
                check(database.mediaStudioDao().getCount() == mediaStudioList.size.toLong())

                Timber.d("${mediaStudioList.size} media studios added to database")
            }.onFailure {
                throw Exception("fillMediaTables failed: studio list not filled!")
            }
        }

        Timber.d("Media predefined tables were saved to database")
        setProgress(lastUpdate)
    }
}
