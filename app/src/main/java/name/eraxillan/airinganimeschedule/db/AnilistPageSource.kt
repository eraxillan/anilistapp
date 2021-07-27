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

package name.eraxillan.airinganimeschedule.db

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import name.eraxillan.airinganimeschedule.api.AnilistApi
import name.eraxillan.airinganimeschedule.model.Media
import name.eraxillan.airinganimeschedule.utilities.NETWORK_PAGE_SIZE

private const val ANILIST_STARTING_PAGE_INDEX = 1

class AnilistPagingSource(
    private val service: AnilistApi
) : PagingSource<Int, Media>() {

    companion object {
        private const val LOG_TAG = "54BE6C87_APS" // APS = AnilistPagingSource
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Media> {
        // Start refresh at first page if undefined
        val page = params.key ?: ANILIST_STARTING_PAGE_INDEX

        return try {
            Log.d(LOG_TAG, "Requesting ${page}st page with ${params.loadSize} items per page...")
            // TODO: how to sync `params.loadSize` here and in `AiringAnimeRepo.remoteAiringAnimeList`?
            val response = service.getAiringAnimeList(page, /*params.loadSize*/ NETWORK_PAGE_SIZE)

            val rateLimit = service.getResponseRateLimit(response)
            Log.d(LOG_TAG, "Rate limit: ${rateLimit.remaining} from ${rateLimit.total}")

            val pagination = service.getResponsePagination(response)
            Log.d(
                LOG_TAG,
                """
                    Anime list size: ${pagination.totalItems},
                    current page: ${pagination.currentPage},
                    items per page: ${pagination.perPage},
                    total pages: ${pagination.totalPages}
                """.trimIndent()
            )

            val serverAnimeList = response.data?.page?.media?.filterNotNull() ?: emptyList()
            val animeList = serverAnimeList.map { medium -> convertAnilistMedia(medium) }

            // FIXME: move to background worker
            service.fillEpisodeCount(serverAnimeList, animeList)

            /*val test = if (pagination.hasNextPage) pagination.currentPage + 1 else null
            Log.d(LOG_TAG, "prevKey=null, nextKey=$test")*/

            LoadResult.Page(
                data = animeList,
                prevKey = null, // only paging forward
                nextKey = if (pagination.hasNextPage) pagination.currentPage + 1 else null
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Media>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
