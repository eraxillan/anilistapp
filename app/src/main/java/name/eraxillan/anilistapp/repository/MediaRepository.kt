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

package name.eraxillan.anilistapp.repository
import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import name.eraxillan.anilistapp.api.AnilistApi
import name.eraxillan.anilistapp.data.*
import name.eraxillan.anilistapp.model.MediaFilter
import name.eraxillan.anilistapp.model.MediaSort
import name.eraxillan.anilistapp.utilities.NETWORK_PAGE_SIZE
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * Repository class that works with local and remote data sources
 */
@Singleton
class MediaRepository @Inject constructor(
    private val database: MediaDatabase,
    private val backend: AnilistApi
) {

    /**
     * Get media list and exposed as a stream of data,
     * that will emit every time we get more data from the network
     */
    fun getMediaListStream(filter: MediaFilter, sortBy: MediaSort): Flow<PagingData<LocalMedia>> {
        Timber.d("Querying media list from remote backend...")

        @OptIn(ExperimentalTime::class)
        val pagingSourceFactory = {
            var result: PagingSource<Int, LocalMedia>
            val queryTime = measureTime {
                result = database.mediaDao().getFilteredAndSortedMediaPaged(filter, sortBy)
            }
            Timber.d("Media list 'SELECT' query execution time: $queryTime")
            result
        }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = NETWORK_PAGE_SIZE),
            remoteMediator = AnilistRemoteMediator(database, backend, filter, sortBy),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}
