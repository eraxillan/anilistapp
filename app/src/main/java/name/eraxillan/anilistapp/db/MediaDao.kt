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

package name.eraxillan.anilistapp.db

import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import name.eraxillan.anilistapp.model.*
import timber.log.Timber


/**
 * Media database CRUD: create-read-update-delete
 * NOTE: currently only airing anime stored
 *
 * `LiveData` objects can be observed by another object.
 * `LiveData` notifies any observers when the data changes.
 * This provides a great way to keep user interface elements up
 * to date when items change in the database.
 * `LiveData` objects do their work in a background thread. By default, Room won’t
 * allow you to make calls to DAO methods on the main thread. By returning `LiveData`
 * objects, your method becomes an asynchronous query, and there is no restriction to
 * calling it from the main thread
 */
@Dao
abstract class MediaDao {
    @Query("SELECT COUNT(*) FROM media_collection")
    abstract fun getAllMediaCount(): Long

    @Transaction
    @Query("SELECT * FROM media_collection ORDER BY romaji_title COLLATE NOCASE ASC")
    protected abstract fun getAllLocalMediaPagedByTitle(): PagingSource<Int, LocalMedia>

    @Transaction
    @Query("SELECT * FROM media_collection ORDER BY popularity DESC, romaji_title COLLATE NOCASE ASC")
    protected abstract fun getAllLocalMediaPagedIByPopularity(): PagingSource<Int, LocalMedia>

    @Transaction
    @Query("SELECT * FROM media_collection ORDER BY average_score DESC, romaji_title COLLATE NOCASE ASC")
    protected abstract fun getAllLocalMediaPagedByAverageScore(): PagingSource<Int, LocalMedia>

    @Transaction
    @Query("SELECT * FROM media_collection ORDER BY trending DESC, romaji_title COLLATE NOCASE ASC")
    protected abstract fun getAllLocalMediaPagedByTrending(): PagingSource<Int, LocalMedia>

    @Transaction
    @Query("SELECT * FROM media_collection ORDER BY favorites DESC, romaji_title COLLATE NOCASE ASC")
    protected abstract fun getAllLocalMediaPagedByFavorites(): PagingSource<Int, LocalMedia>

    @Transaction
    @Query("SELECT * FROM media_collection ORDER BY anilist_id DESC, romaji_title COLLATE NOCASE ASC")
    protected abstract fun getAllLocalMediaPagedByDateAdded(): PagingSource<Int, LocalMedia>

    @Transaction
    @Query("SELECT * FROM media_collection ORDER BY start_date DESC, romaji_title COLLATE NOCASE ASC")
    protected abstract fun getAllLocalMediaPagedByReleaseDate(): PagingSource<Int, LocalMedia>

    fun getAllLocalMediaPaged(sort: MediaSort): PagingSource<Int, LocalMedia> {
        return when (sort) {
            MediaSort.BY_TITLE -> getAllLocalMediaPagedByTitle()
            MediaSort.BY_POPULARITY -> getAllLocalMediaPagedIByPopularity()
            MediaSort.BY_AVERAGE_SCORE -> getAllLocalMediaPagedByAverageScore()
            MediaSort.BY_TRENDING -> getAllLocalMediaPagedByTrending()
            MediaSort.BY_FAVORITES -> getAllLocalMediaPagedByFavorites()
            MediaSort.BY_DATE_ADDED -> getAllLocalMediaPagedByDateAdded()
            MediaSort.BY_RELEASE_DATE -> getAllLocalMediaPagedByReleaseDate()
            else -> {
                Timber.e("Invalid sort value $sort! Sort by `popularity` field as default one")
                getAllLocalMediaPagedIByPopularity()
            }
        }
    }

    // TODO: do not work in current Android Room version, [MediaDatabaseHelper] class do this job
    /*@Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(entities: Collection<LocalMedia>): List<Long>*/

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(entities: Collection<Media>): List<Long>

    @Query("DELETE FROM media_collection")
    abstract suspend fun deleteAll()
}
