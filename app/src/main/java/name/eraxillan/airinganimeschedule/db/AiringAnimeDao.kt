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

package name.eraxillan.airinganimeschedule.db

import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import name.eraxillan.airinganimeschedule.model.Media

/**
 * Airing anime database CRUD: create-read-update-delete
 *
 * LiveData objects can be observed by another object.
 * LiveData notifies any observers when the data changes.
 * This provides a great way to keep user interface elements up
 * to date when items change in the database.
 * LiveData objects do their work in a background thread. By default, Room won’t
 * allow you to make calls to DAO methods on the main thread. By returning LiveData
 * objects, your method becomes an asynchronous query, and there is no restriction to
 * calling it from the main thread.
 */
@Dao
interface AiringAnimeDao {
    @Query("SELECT * FROM media_collection ORDER BY popularity DESC, romajiTitle ASC")
    fun getAiringAnimeListPages(): PagingSource<Int, Media>

    @Insert(onConflict = REPLACE)
    suspend fun insertAllAnime(animeList: List<Media>)

    @Query("DELETE FROM media_collection")
    suspend fun deleteAllAnime()
}
