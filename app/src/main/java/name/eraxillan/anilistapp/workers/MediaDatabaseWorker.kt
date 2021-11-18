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
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import name.eraxillan.anilistapp.R
import name.eraxillan.anilistapp.data.room.MediaDatabase
import name.eraxillan.anilistapp.model.*
import name.eraxillan.anilistapp.utilities.ANIME_DATA_FILENAME
import name.eraxillan.anilistapp.utilities.INIT_DATABASE_WORKER_PROGRESS_KEY
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
                //fillMediaData()
                fillMediaTables()

                Result.success()
            }.onFailure {
                Timber.e(it, "Unable to add mock media list to database")
                Result.failure()
            }

            result.getOrNull() ?: Result.failure()
        }
    }

    private suspend fun fillMediaData() {
        // Load test media list from resources and append to database
        applicationContext.assets.open(ANIME_DATA_FILENAME).use { inputStream ->
            val mediaList = mediaListFromJson(inputStream.reader())
            Timber.d("Mock media list loaded: ${mediaList.size}")

            val database = MediaDatabase.getInstance(applicationContext)
            database.mediaDao().insertAll(mediaList)
            Result.success()
        }
    }

    private suspend fun fillMediaTables() {
        val firstUpdate = workDataOf(INIT_DATABASE_WORKER_PROGRESS_KEY to 0)
        val lastUpdate = workDataOf(INIT_DATABASE_WORKER_PROGRESS_KEY to 100)

        Timber.d("Filling media predefined tables...")
        setProgress(firstUpdate)

        val database = MediaDatabase.getInstance(applicationContext)

        // Load media genre list from resources and append it to database
        val genreStrings = applicationContext.resources.getStringArray(R.array.genres_dialog_genres)
        val genreObjects = genreStrings.mapIndexed { index, element ->
            MediaGenre(genreId = (index + 1).toLong(), name = element)
        }
        check(genreStrings.isNotEmpty() && genreObjects.isNotEmpty())
        database.mediaGenreDao().insertAll(genreObjects)
        Timber.d("${genreStrings.size} media genres added to database")

        // Load media tag list from resources and append it to database
        // FIXME: fill other fields
        val tagStrings = applicationContext.resources.getStringArray(R.array.tags_dialog_tags)
        val tagObjects = tagStrings.mapIndexed { index, element ->
            MediaTag(tagId = (index + 1).toLong(), name = element)
        }
        check(tagStrings.isNotEmpty() && tagObjects.isNotEmpty())
        database.mediaTagDao().insertAll(tagObjects)
        Timber.d("${tagStrings.size} media tags added to database")

        // Load media format list from resources and append it to database
        /*
        val formatStrings = applicationContext.resources.getStringArray(R.array.media_format_enum)
        val formatObjects = formatStrings.mapIndexed { index, element ->
            MediaFormat(formatId = (index + 1).toLong(), format = MediaFormatEnum.valueOf(element))
        }
        check(formatStrings.isNotEmpty() && formatObjects.isNotEmpty())
        database.mediaFormatDao().insertAll(formatObjects)
        Timber.d("${formatStrings.size} media formats added to database")

        // Load media external link list from resources and append it to database
        // TODO: fill URL
        val serviceStrings = applicationContext.resources.getStringArray(R.array.streaming_on_dialog_services)
        val serviceObjects = serviceStrings.mapIndexed { index, element ->
            MediaExternalLink(externalLinkId = (index + 1).toLong(), url = "", site = element)
        }
        check(serviceStrings.isNotEmpty() && serviceObjects.isNotEmpty())
        database.mediaExternalLinkDao().insertAll(serviceObjects)

        // Load media source list from resources and append it to database
        val sourceStrings = applicationContext.resources.getStringArray(R.array.media_source_enum)
        val sourceObjects = sourceStrings.mapIndexed { index, element ->
            MediaSource(sourceId = (index + 1).toLong(), source = MediaSourceEnum.valueOf(element))
        }
        check(serviceStrings.isNotEmpty() && sourceObjects.isNotEmpty())
        database.mediaSourceDao().insertAll(sourceObjects)*/

        Timber.d("Media predefined tables were saved to database")
        setProgress(lastUpdate)
    }
}
