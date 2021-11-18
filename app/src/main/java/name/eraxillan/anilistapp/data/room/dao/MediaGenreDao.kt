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

package name.eraxillan.anilistapp.data.room.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import name.eraxillan.anilistapp.model.MediaGenre


@Dao
abstract class MediaGenreDao {
    @Query("SELECT * FROM media_genres WHERE name = :name")
    abstract suspend fun getGenreWithName(name: String): MediaGenre?

    @Query("SELECT * FROM media_genres")
    abstract suspend fun getAll(): List<MediaGenre>

    @Query("SELECT COUNT(genre_id) FROM media_genres")
    abstract suspend fun getCount(): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(entity: MediaGenre): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(vararg entity: MediaGenre): List<Long>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(entities: Collection<MediaGenre>): List<Long>

    @Update(onConflict = REPLACE)
    abstract suspend fun update(entity: MediaGenre)

    @Delete
    abstract suspend fun delete(entity: MediaGenre): Int
}
