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

package name.eraxillan.airinganimeschedule.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import name.eraxillan.airinganimeschedule.api.AnilistApi
import name.eraxillan.airinganimeschedule.db.MediaDatabase
import name.eraxillan.airinganimeschedule.db.RemoteKeys
import name.eraxillan.airinganimeschedule.db.convertAnilistMedia
import name.eraxillan.airinganimeschedule.model.Media
import name.eraxillan.airinganimeschedule.utilities.NETWORK_PAGE_SIZE
import java.io.IOException
import java.lang.Exception


typealias MediaPagingState = PagingState<Int, Media>

// Anilist page API is 1 based: https://anilist.gitbook.io/anilist-apiv2-docs/overview/graphql/pagination
private const val ANILIST_STARTING_PAGE_INDEX = 1

@ExperimentalPagingApi
class AnilistRemoteMediator(
    private val database: MediaDatabase,
    private val backend: AnilistApi
) : RemoteMediator<Int, Media>() {

    companion object {
        private const val LOG_TAG = "54BE6C87_Mediator"
    }

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH

        // TODO: implement caching with timeout, because media information can be updated on server side
        /*
        val cacheTimeout = TimeUnit.HOURS.convert(1, TimeUnit.MILLISECONDS)
        return if (System.currentTimeMillis() - mediaDao.lastUpdated() >= cacheTimeout) {
            // Cached data is up-to-date, so there is no need to re-fetch from network
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network;
            // returning LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
            // APPEND and PREPEND from running until REFRESH succeeds
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }*/
    }

    override suspend fun load(loadType: LoadType, state: MediaPagingState): MediatorResult {
        val page = when (loadType) {
            REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                val temp = remoteKeys?.nextKey?.minus(1) ?: ANILIST_STARTING_PAGE_INDEX
                Log.d(LOG_TAG, "Got LoadType.REFRESH request: pageNo=$temp")
                temp
            }
            PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If `remoteKeys` is null, that means the refresh result is not in the database yet.
                // We can return `Success` with `endOfPaginationReached = false` because Paging
                // will call this method again if `RemoteKeys` becomes non-null.
                // If `remoteKeys` is NOT NULL but its `prevKey` is null, that means we've reached
                // the end of pagination for prepend
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = (remoteKeys != null))
                Log.d(LOG_TAG, "Got LoadType.PREPEND request: pageNo=$prevKey")
                prevKey
            }
            APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If `remoteKeys` is null, that means the refresh result is not in the database yet.
                // We can return `Success` with `endOfPaginationReached = false` because Paging
                // will call this method again if `RemoteKeys` becomes non-null.
                // If `remoteKeys` is NOT NULL but its `prevKey` is null, that means we've reached
                // the end of pagination for append
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = (remoteKeys != null))
                Log.d(LOG_TAG, "Got LoadType.APPEND request: pageNo=$nextKey")
                nextKey
            }
        }

        try {
            check(NETWORK_PAGE_SIZE == state.config.pageSize)
            val pageSize = when (loadType) {
                REFRESH -> state.config.initialLoadSize
                else -> state.config.pageSize
            }
            Log.d(LOG_TAG, "Querying server: pageNo=$page with $pageSize per page...")
            val backendResponse = backend.getAiringAnimeList(page, pageSize)
            Log.d(LOG_TAG, "Successfully got response from server")

            val rateLimit = backend.getResponseRateLimit(backendResponse)
            Log.d(
                LOG_TAG,
                "Network query rate limit status: ${rateLimit.remaining} from ${rateLimit.total}"
            )

            val pagination = backend.getResponsePagination(backendResponse)
            val endOfPaginationReached = !pagination.hasNextPage
            Log.d(
                LOG_TAG,
                """
                    Media list total size: ${pagination.totalItems},
                    current page: ${pagination.currentPage},
                    items per page: ${pagination.perPage},
                    total pages: ${pagination.totalPages}
                    end of pagination reached: $endOfPaginationReached
                """.trimIndent()
            )

            val serverMediaList = backendResponse.data?.page?.media?.filterNotNull() ?: emptyList()
            val mediaList = serverMediaList.map { medium -> convertAnilistMedia(medium) }

            // FIXME: move to background worker
            backend.fillEpisodeCount(serverMediaList, mediaList)

            Log.d(LOG_TAG, "mediaList.size=${mediaList.size}")

            database.withTransaction {
                // Clear all tables in the database
                if (loadType == REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.mediaDao().deleteAllMedia()
                    Log.d(LOG_TAG, "Cache database cleared!")
                }

                val prevKey = if (page == ANILIST_STARTING_PAGE_INDEX) null else (page - 1)
                // RemoteMediator.load with loadType=APPEND uses pageSize=state.config.pageSize,
                // so nextKey should always be computed based on increments of state.config.pageSize.
                // E.g., If we load items 0-59 on initial load with key=1, the nextKey should
                // not be 2, because that would load items 20-39, which overlaps our initial load
                // instead of fetching new data as intended
                val nextKey = if (endOfPaginationReached) null else page + (pageSize / state.config.pageSize)
                Log.d(LOG_TAG, "prevKey=$prevKey, nextKey=$nextKey")

                val keys = mediaList.map {
                    RemoteKeys(anilistId = it.anilistId, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao().insertAll(keys)
                database.mediaDao().insertMediaList(mediaList)
                Log.d(
                    LOG_TAG,
                    "Cache updated: ${keys.size} keys and ${mediaList.size} records added"
                )
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            Log.e(LOG_TAG, "Unable to fetch data from network (IOException): ${exception.localizedMessage}")
            return MediatorResult.Error(exception)
        } catch (exception: java.net.UnknownHostException) {
            Log.e(LOG_TAG, "Unable to fetch data from network (UnknownHostException): ${exception.localizedMessage}")
            return MediatorResult.Error(exception)
        } catch (exception: Exception) {
            Log.e(LOG_TAG, "Unable to fetch data from network (unknown exception): ${exception.localizedMessage}")
            return MediatorResult.Error(exception)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private suspend fun getRemoteKeyForLastItem(state: MediaPagingState): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { media ->
                // Get the remote keys of the last item retrieved
                database.remoteKeysDao().remoteKeysById(media.anilistId)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: MediaPagingState): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { media ->
                // Get the remote keys of the first items retrieved
                database.remoteKeysDao().remoteKeysById(media.anilistId)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: MediaPagingState): RemoteKeys? {
        // The paging library is trying to load data after the anchor position.
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.anilistId?.let { anilistId ->
                database.remoteKeysDao().remoteKeysById(anilistId)
            }
        }
    }
}
