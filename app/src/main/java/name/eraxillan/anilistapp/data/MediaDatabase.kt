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

package name.eraxillan.anilistapp.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import name.eraxillan.anilistapp.data.room.dao.*
import name.eraxillan.anilistapp.model.*
import name.eraxillan.anilistapp.utilities.DATABASE_NAME
import name.eraxillan.anilistapp.utilities.INIT_DATABASE_WORKER_TAG
import name.eraxillan.anilistapp.workers.MediaDatabaseWorker

/**
 * The Room database for this app
 */
@Database(
    entities = [
        MediaSource::class,
        MediaFormat::class,

        MediaExternalLink::class,
        MediaGenre::class,
        MediaGenreEntry::class,
        MediaRank::class,
        MediaStreamingEpisode::class,
        MediaStudio::class,
        MediaStudioEntry::class,
        MediaTag::class,
        MediaTagEntry::class,
        MediaTitleSynonym::class,

        Media::class,
        FavoriteMedia::class,
        RemoteKeys::class
    ],
    views = [
        MediaWithGenres::class,
        MediaWithTags::class,
        MediaWithServices::class
    ],
    version = 8,
    //exportSchema = false
)
@TypeConverters(DatabaseTypeConverters::class)
abstract class MediaDatabase : RoomDatabase() {
    abstract fun mediaTitleSynonymDao(): MediaTitleSynonymDao
    abstract fun mediaExternalLinkDao(): MediaExternalLinkDao
    abstract fun mediaStreamingEpisodeDao(): MediaStreamingEpisodeDao
    abstract fun mediaRankingDao(): MediaRankingDao

    abstract fun mediaGenreDao(): MediaGenreDao
    abstract fun mediaTagDao(): MediaTagDao
    abstract fun mediaStudioDao(): MediaStudioDao

    abstract fun mediaGenreEntryDao(): MediaGenreEntryDao
    abstract fun mediaTagEntryDao(): MediaTagEntryDao
    abstract fun mediaStudioEntryDao(): MediaStudioEntryDao

    abstract fun mediaDao(): MediaDao
    abstract fun favoriteDao(): FavoriteMediaDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    // Singleton object
    companion object {
        @Volatile
        private var instance: MediaDatabase? = null

        fun getInstance(context: Context): MediaDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): MediaDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MediaDatabase::class.java,
                DATABASE_NAME
            ).addCallback(
                object : RoomDatabase.Callback() {
                    private var workerWasStarted = false

                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        if (!workerWasStarted) runWorker()
                    }

                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        super.onDestructiveMigration(db)

                        if (!workerWasStarted) runWorker()
                    }

                    private fun runWorker() {
                        val request = OneTimeWorkRequestBuilder<MediaDatabaseWorker>()
                            .addTag(INIT_DATABASE_WORKER_TAG)
                            .build()

                        WorkManager.getInstance(context).enqueue(request)
                        workerWasStarted = true
                    }
                }
            ).fallbackToDestructiveMigration(
            ).build()
        }
    }
}
