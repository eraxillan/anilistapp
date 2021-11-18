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

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import name.eraxillan.anilistapp.model.MediaStudio


@Dao
abstract class MediaStudioDao {
    @Query("SELECT * FROM media_studios WHERE name = :name")
    abstract suspend fun getStudioWithName(name: String): MediaStudio?

    @Query("SELECT studio_id FROM media_studios WHERE studio_id IN (:ids)")
    abstract suspend fun getExistingIds(ids: List<Long>): List<Long>

    @Query("SELECT * FROM media_studios")
    abstract suspend fun getAll(): List<MediaStudio>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(entity: MediaStudio): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(vararg entity: MediaStudio): List<Long>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(entities: Collection<MediaStudio>): List<Long>

    @Update(onConflict = REPLACE)
    abstract suspend fun update(entity: MediaStudio)

    @Delete
    abstract suspend fun delete(entity: MediaStudio): Int

    @Query("DELETE FROM media_studios")
    abstract suspend fun deleteAll()
}
