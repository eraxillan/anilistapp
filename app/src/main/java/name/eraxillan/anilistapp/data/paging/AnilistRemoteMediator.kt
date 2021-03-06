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

package name.eraxillan.anilistapp.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.apollographql.apollo.api.Response
import name.eraxillan.anilistapp.MediaListQuery
import name.eraxillan.anilistapp.api.AnilistApi
import name.eraxillan.anilistapp.api.convertAnilistMedia
import name.eraxillan.anilistapp.api.convertRemoteMedia
import name.eraxillan.anilistapp.data.room.LocalMediaWithRelations
import name.eraxillan.anilistapp.data.room.MediaDatabase
import name.eraxillan.anilistapp.data.room.MediaDatabaseHelper
import name.eraxillan.anilistapp.data.room.RemoteKeys
import name.eraxillan.anilistapp.model.*
import name.eraxillan.anilistapp.utilities.isWinterSeasonBegin
import timber.log.Timber
import java.io.IOException
import java.lang.Exception


typealias MediaPagingState = PagingState<Int, LocalMediaWithRelations>

// Anilist page API is 1 based: https://anilist.gitbook.io/anilist-apiv2-docs/overview/graphql/pagination
private const val ANILIST_STARTING_PAGE_INDEX = 1

@ExperimentalPagingApi
class AnilistRemoteMediator(
    private val database: MediaDatabase,
    private val backend: AnilistApi,
    private val filter: MediaFilter,
    private val sortBy: MediaSort
) : RemoteMediator<Int, LocalMediaWithRelations>() {

    private val databaseHelper: MediaDatabaseHelper = MediaDatabaseHelper(database)

    private suspend fun queryMedia(response: Response<MediaListQuery.Data>) : Pair<MutableList<RemoteMedia>, Boolean> {
        val pagination = backend.getResponsePagination(response)
        val endOfPaginationReached = pagination.totalItems == 0 || !pagination.hasNextPage
                || response.data?.page?.media?.isEmpty() == true

        val serverMediaList = response.data?.page?.media?.filterNotNull() ?: emptyList()
        val mediaList = serverMediaList.map { medium -> convertAnilistMedia(medium) }.toMutableList()

        // FIXME: move to background worker
        backend.fillEpisodeCount(serverMediaList, mediaList)

        Timber.d(
            """
            Media list total size: ${pagination.totalItems},
            media list for current page size: ${mediaList.size},
            current page: ${pagination.currentPage},
            items per page: ${pagination.perPage},
            total pages: ${pagination.totalPages}
            end of pagination reached: $endOfPaginationReached
            """.trimIndent()
        )

        return mediaList to endOfPaginationReached
    }

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH

        // TODO: implement caching with timeout using `updatedAt` field,
        //  because media information can be updated on server side
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
        // https://kotlinlang.org/docs/destructuring-declarations.html#example-destructuring-declarations-and-maps
        val (page, isEmpty, endOfPaginationReachedRemote) = getRemoteKey(loadType, state)
        if (isEmpty)
            return MediatorResult.Success(endOfPaginationReachedRemote)

        try {
            val pageSize = state.config.pageSize
            /*val pageSize = when (loadType) {
                REFRESH -> state.config.initialLoadSize
                else -> state.config.pageSize
            }*/
            Timber.d("Querying server: pageNo=$page with $pageSize per page...")
            val backendResponse = backend.getMediaList(page, pageSize, filter, sortBy)
            Timber.d("Successfully got response from server")

            var (mediaList, endOfPaginationReached) = queryMedia(backendResponse)

            // The Winter season last from December to February, so we should include next year too;
            // Anilist API don't allow to specify a range of years, so we have to sent the second request
            if (isWinterSeasonBegin()) {
                val winterFilter = MediaFilter.withNextYear(filter)
                val winterBackendResponse = backend.getMediaList(page, pageSize, winterFilter, sortBy)

                val (winterMediaList, winterEndOfPaginationReached) = queryMedia(winterBackendResponse)
                mediaList.addAll(winterMediaList)

                if (!winterEndOfPaginationReached && endOfPaginationReached) {
                    endOfPaginationReached = false
                }
            }

            saveMediasToDatabase(
                loadType, page,
                /*pageSize, state.config.pageSize,*/
                endOfPaginationReached,
                mediaList.map { convertRemoteMedia(it) }
            )
            return MediatorResult.Success(endOfPaginationReached)
        } catch (exception: IOException) {
            Timber.e("Unable to fetch data from network (IOException): ${exception.localizedMessage}")
            return MediatorResult.Error(exception)
        } catch (exception: java.net.UnknownHostException) {
            Timber.e("Unable to fetch data from network (UnknownHostException): ${exception.localizedMessage}")
            return MediatorResult.Error(exception)
        } catch (exception: Exception) {
            Timber.e("Unable to fetch data from network (unknown exception): ${exception.localizedMessage}")
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
                database.remoteKeysDao().remoteKeysById(media.localMedia.anilistId)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: MediaPagingState): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { media ->
                // Get the remote keys of the first items retrieved
                database.remoteKeysDao().remoteKeysById(media.localMedia.anilistId)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: MediaPagingState): RemoteKeys? {
        // The paging library is trying to load data after the anchor position.
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.localMedia?.anilistId?.let { anilistId ->
                database.remoteKeysDao().remoteKeysById(anilistId)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private suspend fun insertRemoteKeys(
        page: Int,
        /*pageSize: Int, statePageSize: Int,*/
        endOfPaginationReached: Boolean,
        mediaList: List<LocalMediaWithRelations>
    ): Int {
        val prevKey = if (page == ANILIST_STARTING_PAGE_INDEX) null else (page - 1)

        // RemoteMediator.load with loadType=APPEND uses pageSize=state.config.pageSize,
        // so nextKey should always be computed based on increments of state.config.pageSize.
        // E.g., If we load items 0-59 on initial load with key=1, the nextKey should
        // not be 2, because that would load items 20-39, which overlaps our initial load
        // instead of fetching new data as intended
        val nextKey = if (endOfPaginationReached) null else page + 1 /*(pageSize / statePageSize)*/
        Timber.d("prevKey=$prevKey, nextKey=$nextKey")

        val keys = mediaList.map {
            RemoteKeys(anilistId = it.localMedia.anilistId, prevKey = prevKey, nextKey = nextKey)
        }
        database.remoteKeysDao().insertAll(keys)
        Timber.d("${keys.size} remoteKeys inserted to database")

        return keys.size
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private suspend fun saveMediasToDatabase(
        loadType: LoadType, page: Int,
        /*pageSize: Int, statePageSize: Int,*/
        endOfPaginationReached: Boolean,
        mediaList: List<LocalMediaWithRelations>
    ) {
        database.withTransaction {
            if (loadType == REFRESH) {
                Timber.e("Got REFRESH request: clear the database")
                databaseHelper.deleteAllMedias()
            }

            val keysSize = insertRemoteKeys(
                page,
                /*pageSize, statePageSize,*/
                endOfPaginationReached,
                mediaList
            )
            check(keysSize == mediaList.size)

            val mediaIds = database.mediaDao().insertAll(mediaList.map { it.localMedia })
            check(mediaIds.size == mediaList.size) { Timber.e("mediaIds.size != mediaList.size") }

            for (i in mediaIds.indices) {
                val mediaId = mediaIds[i]
                check(mediaId != -1L) { Timber.e("mediaId == -1") }

                val media = mediaList[i]

                // One-to-many relations
                databaseHelper.insertTitleSynonyms(media.titleSynonyms, mediaId)
                databaseHelper.insertExternalLinks(media.externalLinks, mediaId)
                databaseHelper.insertStreamingEpisodes(media.streamingEpisodes, mediaId)
                databaseHelper.insertRankings(media.rankings, mediaId)

                // Many-to-many relations
                databaseHelper.insertGenres(media.genres, mediaId)
                databaseHelper.insertTags(media.tags, mediaId)
                databaseHelper.insertStudios(media.studios, mediaId)
            }

            Timber.d(
                "Cache updated: $keysSize keys and ${mediaList.size} records added"
            )
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    data class PageResult(val page: Int, val isEmpty: Boolean, val endOfPaginationReached: Boolean)

    private suspend fun getRemoteKey(loadType: LoadType, state: MediaPagingState): PageResult {
        val page = when (loadType) {
            REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                val temp = remoteKeys?.nextKey?.minus(1) ?: ANILIST_STARTING_PAGE_INDEX

                Timber.d("Got LoadType.REFRESH request: pageNo=$temp")
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
                    ?: return PageResult(-1, true, remoteKeys != null)

                Timber.d("Got LoadType.PREPEND request: pageNo=$prevKey")
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
                    ?: return PageResult(-1, true, remoteKeys != null)

                Timber.d("Got LoadType.APPEND request: pageNo=$nextKey")
                nextKey
            }
        }

        return PageResult(page, isEmpty = false, endOfPaginationReached = false)
    }
}
