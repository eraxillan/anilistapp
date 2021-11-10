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
import name.eraxillan.anilistapp.model.MediaStreamingEpisode


@Dao
abstract class MediaStreamingEpisodeDao {
    @Query("SELECT * FROM media_streaming_episodes WHERE title = :title")
    abstract suspend fun getSynonymWithTitle(title: String): MediaStreamingEpisode?

    @Query("SELECT * FROM media_streaming_episodes")
    abstract suspend fun getAll(): List<MediaStreamingEpisode>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(entity: MediaStreamingEpisode): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(vararg entity: MediaStreamingEpisode): List<Long>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(entities: Collection<MediaStreamingEpisode>): List<Long>

    @Update(onConflict = REPLACE)
    abstract suspend fun update(entity: MediaStreamingEpisode)

    @Delete
    abstract suspend fun delete(entity: MediaStreamingEpisode): Int

    @Query("DELETE FROM media_streaming_episodes")
    abstract suspend fun deleteAll()
}
