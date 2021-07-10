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
import name.eraxillan.airinganimeschedule.db.AiringAnimeDatabase
import name.eraxillan.airinganimeschedule.db.RemoteKeys
import name.eraxillan.airinganimeschedule.db.mediumToAiringAnime
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.utilities.NETWORK_PAGE_SIZE
import java.io.IOException
import java.lang.Exception


typealias AnimePagingState = PagingState<Int, AiringAnime>

// Anilist page API is 1 based: https://anilist.gitbook.io/anilist-apiv2-docs/overview/graphql/pagination
private const val ANILIST_STARTING_PAGE_INDEX = 1

@ExperimentalPagingApi
class AnilistRemoteMediator(
    private val database: AiringAnimeDatabase,
    private val backend: AnilistApi
) : RemoteMediator<Int, AiringAnime>() {

    companion object {
        private const val LOG_TAG = "54BE6C87_ARM" // ARM = AnilistRemoteMediator
    }

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH

        // TODO: implement caching with timeout, because anime information can be updated on server side
        /*
        val cacheTimeout = TimeUnit.HOURS.convert(1, TimeUnit.MILLISECONDS)
        return if (System.currentTimeMillis() - animeDao.lastUpdated() >= cacheTimeout) {
            // Cached data is up-to-date, so there is no need to re-fetch from network
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network;
            // returning LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
            // APPEND and PREPEND from running until REFRESH succeeds
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }*/
    }

    override suspend fun load(loadType: LoadType, state: AnimePagingState): MediatorResult {
        val page = when (loadType) {
            REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                val temp = remoteKeys?.nextKey?.minus(1) ?: ANILIST_STARTING_PAGE_INDEX
                Log.d(LOG_TAG, "Got LoadType.REFRESH request: pageNo = $temp")
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
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                Log.d(LOG_TAG, "Got LoadType.PREPEND request: pageNo = $prevKey")
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
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                Log.d(LOG_TAG, "Got LoadType.APPEND request: pageNo = $nextKey")
                nextKey
            }
        }

        try {
            Log.d(LOG_TAG, "Querying server: pageNo=$page with $NETWORK_PAGE_SIZE per page")
            check(NETWORK_PAGE_SIZE == state.config.pageSize)
            val apiResponse =
                backend.getAiringAnimeList(page, state.config.pageSize /*NETWORK_PAGE_SIZE*/)

            val serverAnimeList = apiResponse.data?.page?.media?.filterNotNull() ?: emptyList()
            val animeList = serverAnimeList.map { medium -> mediumToAiringAnime(medium) }

            val endOfPaginationReached = apiResponse.data?.page?.pageInfo?.hasNextPage == false

            Log.d(LOG_TAG, "Got response from server: animeList.size=${animeList.size}")
            Log.d(LOG_TAG, "End of pagination reached: $endOfPaginationReached")

            database.withTransaction {
                // Clear all tables in the database
                if (loadType == REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.airingDao().deleteAllAnime()
                    Log.d(LOG_TAG, "Cache database cleared!")
                }

                val prevKey = if (page == ANILIST_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                Log.d(LOG_TAG, "prevKey=$prevKey, nextKey=$nextKey")

                val keys = animeList.map {
                    RemoteKeys(anilistId = it.anilistId, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao().insertAll(keys)
                database.airingDao().insertAllAnime(animeList)
                Log.d(LOG_TAG, "Cache filled: ${keys.size} keys and ${animeList.size} records added")
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            Log.e(LOG_TAG, "Unable to fetch data from network: ${exception.localizedMessage}")
            return MediatorResult.Error(exception)
        } catch (exception: java.net.UnknownHostException) {
            Log.e(LOG_TAG, "Unable to fetch data from network: ${exception.localizedMessage}")
            return MediatorResult.Error(exception)
        } catch (exception: Exception) {
            Log.e(LOG_TAG, "Unable to fetch data from network: ${exception.localizedMessage}")
            return MediatorResult.Error(exception)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private suspend fun getRemoteKeyForLastItem(state: AnimePagingState): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { anime ->
                // Get the remote keys of the last item retrieved
                database.remoteKeysDao().remoteKeysAnimeId(anime.anilistId)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: AnimePagingState): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { anime ->
                // Get the remote keys of the first items retrieved
                database.remoteKeysDao().remoteKeysAnimeId(anime.anilistId)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: AnimePagingState): RemoteKeys? {
        // The paging library is trying to load data after the anchor position.
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.anilistId?.let { anilistId ->
                database.remoteKeysDao().remoteKeysAnimeId(anilistId)
            }
        }
    }
}
