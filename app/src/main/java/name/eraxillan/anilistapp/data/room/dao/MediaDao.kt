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

import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import name.eraxillan.anilistapp.data.room.LocalMedia
import name.eraxillan.anilistapp.data.room.LocalMediaWithRelations
import name.eraxillan.anilistapp.model.*


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
    @Query(
        """
        SELECT * FROM media_collection WHERE
            -- TODO: implement fuzzy full text search like Anilist backend do
            (:search IS NULL OR :search = '' OR romaji_title LIKE '%' || :search || '%') AND
            (:year IS NULL OR :year = 0 OR start_season_year = :year) AND
            (:country IS NULL OR :country = '' OR country_of_origin = :country) AND            
            (:season IS NULL OR :season = 'UNKNOWN' OR start_season = :season) AND
            (:status IS NULL OR :status = 'UNKNOWN' OR status = :status) AND
            (:isLicensed IS NULL OR is_licensed = :isLicensed) AND
            (:formatsCount = 0 OR format IN (:formats)) AND            
            (:sourcesCount = 0 OR source IN (:sources)) AND
            anilist_id IN (
                SELECT anilist_id FROM media_with_genres
                WHERE :genresCount = 0 OR genre_name IN (:genres) GROUP BY anilist_id
                HAVING COUNT(*) = :genresCount OR :genresCount = 0
                INTERSECT
                SELECT anilist_id FROM media_with_tags
                WHERE :tagsCount = 0 OR tag_name IN (:tags) GROUP BY anilist_id
                HAVING COUNT(*) = :tagsCount OR :tagsCount = 0
                INTERSECT
                SELECT anilist_id FROM media_with_services
                WHERE :servicesCount = 0 OR service_name IN (:services) GROUP BY anilist_id
                --HAVING COUNT(*) = :servicesCount OR :servicesCount = 0
            )
            ORDER BY
            CASE WHEN :sortOption = 1 THEN popularity END DESC,
            CASE WHEN :sortOption = 2 THEN average_score END DESC,
            CASE WHEN :sortOption = 3 THEN trending END DESC,
            CASE WHEN :sortOption = 4 THEN favorites END DESC,
            CASE WHEN :sortOption = 5 THEN anilist_id END DESC,
            CASE WHEN :sortOption = 6 THEN start_date END DESC,
            romaji_title COLLATE NOCASE ASC
    """
    )
    protected abstract fun getFilteredMediaInternalPaged(
        search: String? = null, year: Int? = null, season: MediaSeason? = null,
        formats: List<MediaFormatEnum> = emptyList(), formatsCount: Int = 0,
        status: MediaStatus? = null, country: MediaCountry? = null,
        sources: List<MediaSourceEnum> = emptyList(), sourcesCount: Int = 0,
        isLicensed: Boolean? = null,
        genres: List<String> = emptyList(), genresCount: Int = 0,
        tags: List<String> = emptyList(), tagsCount: Int = 0,
        services: List<String> = emptyList(), servicesCount: Int = 0,
        sortOption: Int = 0
    ): PagingSource<Int, LocalMediaWithRelations>

    fun getFilteredAndSortedMediaPaged(
        filter: MediaFilter,
        sort: MediaSort
    ): PagingSource<Int, LocalMediaWithRelations> {
        return getFilteredMediaInternalPaged(
            search = filter.search, year = filter.year, season = filter.season,
            formats = filter.formats.orEmpty(), formatsCount = filter.formats?.size ?: 0,
            status = filter.status, country = filter.country,
            sources = filter.sources.orEmpty(), sourcesCount = filter.sources?.size ?: 0,
            isLicensed = filter.isLicensed,
            genres = filter.genres.orEmpty(), genresCount = filter.genres?.size ?: 0,
            tags = filter.tags.orEmpty(), tagsCount = filter.tags?.size ?: 0,
            services = filter.services.orEmpty(), servicesCount = filter.services?.size ?: 0,
            sortOption = sort.ordinal
        )
    }

    // TODO: do not work in current Android Room version, [MediaDatabaseHelper] class do this job
    /*@Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(entities: Collection<LocalMediaWithRelations>): List<Long>*/

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(entities: Collection<LocalMedia>): List<Long>

    @Query("DELETE FROM media_collection")
    abstract suspend fun deleteAll()
}
