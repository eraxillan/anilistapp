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
import name.eraxillan.anilistapp.model.*

@Dao
abstract class MediaExternalLinkDao {
    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(entity: MediaExternalLink): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(vararg entity: MediaExternalLink): List<Long>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(entities: Collection<MediaExternalLink>): List<Long>

    @Update(onConflict = REPLACE)
    abstract suspend fun update(entity: MediaExternalLink)

    @Delete
    abstract suspend fun delete(entity: MediaExternalLink): Int

    @Query("DELETE FROM media_external_links")
    abstract suspend fun deleteAll()
}
