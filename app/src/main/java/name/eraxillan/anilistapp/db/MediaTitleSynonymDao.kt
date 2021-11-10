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

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import name.eraxillan.anilistapp.model.MediaTitleSynonym


@Dao
abstract class MediaTitleSynonymDao {
    @Query("SELECT * FROM media_title_synonyms WHERE name = :name")
    abstract suspend fun getSynonymWithName(name: String): MediaTitleSynonym?

    @Query("SELECT * FROM media_title_synonyms")
    abstract suspend fun getAll(): List<MediaTitleSynonym>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(entity: MediaTitleSynonym): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(vararg entity: MediaTitleSynonym): List<Long>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(entities: Collection<MediaTitleSynonym>): List<Long>

    @Update(onConflict = REPLACE)
    abstract suspend fun update(entity: MediaTitleSynonym)

    @Delete
    abstract suspend fun delete(entity: MediaTitleSynonym): Int

    @Query("DELETE FROM media_title_synonyms")
    abstract suspend fun deleteAll()
}
