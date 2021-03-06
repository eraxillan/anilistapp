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
import name.eraxillan.anilistapp.model.MediaTag


@Dao
abstract class MediaTagDao {
    @Query("SELECT * FROM media_tags WHERE name = :name")
    abstract suspend fun getTagWithName(name: String): MediaTag?

    @Query("SELECT * FROM media_tags")
    abstract suspend fun getAll(): List<MediaTag>

    @Query("SELECT COUNT(tag_id) FROM media_tags")
    abstract suspend fun getCount(): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(entity: MediaTag): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(vararg entity: MediaTag): List<Long>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(entities: Collection<MediaTag>): List<Long>

    @Update(onConflict = REPLACE)
    abstract suspend fun update(entity: MediaTag)

    @Delete
    abstract suspend fun delete(entity: MediaTag): Int
}
