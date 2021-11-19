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
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import name.eraxillan.anilistapp.data.room.relations.MediaStudioEntry


@Dao
abstract class MediaStudioEntryDao {
    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(entity: MediaStudioEntry): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(vararg entity: MediaStudioEntry): List<Long>

    @Insert(onConflict = IGNORE)
    abstract suspend fun insertAll(entities: Collection<MediaStudioEntry>): List<Long>

    @Update(onConflict = REPLACE)
    abstract suspend fun update(entity: MediaStudioEntry)

    @Delete
    abstract suspend fun delete(entity: MediaStudioEntry): Int

    @Query("DELETE FROM media_studio_entries")
    abstract suspend fun deleteAll()
}
