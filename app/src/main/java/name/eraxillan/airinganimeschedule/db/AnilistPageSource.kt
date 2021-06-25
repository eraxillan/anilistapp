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
import androidx.core.text.isDigitsOnly
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.api.ApolloExperimental
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.http.OkHttpExecutionContext
import kotlinx.coroutines.delay
import name.eraxillan.airinganimeschedule.AiringAnimeQuery
import name.eraxillan.airinganimeschedule.AnimeRelationsQuery
import name.eraxillan.airinganimeschedule.api.AnilistApi
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.type.MediaRelation
import name.eraxillan.airinganimeschedule.type.MediaStatus
import name.eraxillan.airinganimeschedule.type.MediaType
import java.lang.NumberFormatException

private const val ANILIST_STARTING_PAGE_INDEX = 1

class AnilistPagingSource(
    private val service: AnilistApi
) : PagingSource<Int, AiringAnime>() {

    companion object {
        private const val LOG_TAG = "54BE6C87_APS" // APS = AnilistPagingSource
    }

    private fun getPrequel(medium: AnimeRelationsQuery.Medium): Pair<Boolean, Int> {
        val prequelEdge = medium.relations?.edges?.filterNotNull()?.find { edge ->
            // Anime can have only one prequel and have not at all
            if (edge.relationType == MediaRelation.PREQUEL &&
                edge.node?.type == MediaType.ANIME &&
                edge.node.status == MediaStatus.FINISHED
            ) {
                val title = edge.node.title?.romaji
                Log.d(LOG_TAG, "Prequel anime found: $title")
                true
            } else
                false
        }

        val prequelId = prequelEdge?.node?.id ?: -1
        return Pair(prequelEdge != null, prequelId)
    }

    // FIXME: create a pool of requests implementing 90 req/sec limit
    private tailrec suspend fun searchPrequels(relations: Map<Int, MutableList<Int>>): Int {
        // Free AniList API rate limit restricted with just 90 req/sec!
        // So we need to make as few requests as possible.
        // To achieve this, query all airing anime relations in one request using `id_in` argument

        if (relations.isEmpty()) return 0

        // Recursion stop condition: all anime in list marked with special stop-value (-1)
        val finishedCount = relations.count { entry -> entry.value.last() == -1 }
        Log.d(LOG_TAG, "Relations search progress: $finishedCount from ${relations.size}")
        if (finishedCount == relations.size) return 0

        val ids = relations.map { entry -> entry.value.last() }.filter { entry -> entry != -1 }
        Log.d(LOG_TAG, "Remaining anime ids: ${ids.toString()}")

        val responseInner = service.getAnimeRelations(ids = ids, page = 1, perPage = 30)
        getResponseRateLimit(responseInner)
        delay(100)

        if (responseInner.hasErrors()) {
            Log.e(LOG_TAG, "Relations request failed: `${responseInner.errors.toString()}`!")
            return -1
        }
        Log.d(LOG_TAG, "Relations response succeed: ${responseInner.data?.page?.media?.size} anime found")
        if (responseInner.data?.page?.media?.isEmpty() == true) return -1

        val animeList = responseInner.data?.page?.media?.filterNotNull() ?: emptyList()
        animeList.forEach { medium ->
            Log.d(LOG_TAG, "Calculation episode count for anime '${medium.title?.romaji}'...")

            // Find key with value
            val parent = relations.entries.find { entry -> entry.value.indexOf(medium.id) != -1 }
            val id = parent?.key ?: medium.id
            check(relations.containsKey(id))

            val (hasPrequel, prequelId) = getPrequel(medium)
            if (hasPrequel) {
                relations[id]?.add(prequelId)
            } else {
                relations[id]?.add(-1)
            }
        }

        return searchPrequels(relations)
    }

    data class AnilistRateLimit(val total: Int, val remaining: Int)

    @ApolloExperimental
    private fun <T> getResponseRateLimit(response: Response<T>): AnilistRateLimit {
        val DEFAULT_RATE_LIMIT = 90
        val RATE_LIMIT_TOTAL_KEY = "X-RateLimit-Limit"
        val RATE_LIMIT_REMAINING_KEY = "X-RateLimit-Remaining"

        // See https://anilist.gitbook.io/anilist-apiv2-docs/overview/rate-limiting for details
        val httpContext = response.executionContext[OkHttpExecutionContext.KEY]
        val headers = httpContext?.response?.headers()

        headers?.names()?.forEach { name ->
            Log.d(LOG_TAG, "Response HTTP header: '${name}' => '${headers.get(name)}'")
        }

        val totalStr = headers?.get(RATE_LIMIT_TOTAL_KEY).orEmpty()
        val remainingStr = headers?.get(RATE_LIMIT_REMAINING_KEY).orEmpty()

        val total = try {
            if (totalStr.isEmpty() || !totalStr.isDigitsOnly()) DEFAULT_RATE_LIMIT else totalStr.toInt()
        } catch (exc: NumberFormatException) {
            Log.e(LOG_TAG, "Invalid total rate limit value: '$totalStr'!")
            DEFAULT_RATE_LIMIT
        }

        val remaining = try {
            if (remainingStr.isEmpty() || !remainingStr.isDigitsOnly()) 0 else remainingStr.toInt()
        } catch (exc: NumberFormatException) {
            Log.e(LOG_TAG, "Invalid remaining rate limit value: '$remainingStr'!")
            0
        }

        Log.d(LOG_TAG, "Rate limit: $remaining from $total")
        return AnilistRateLimit(total, remaining)
    }

    data class AnilistPagination(val totalItems: Int, val currentPage: Int, val perPage: Int, val totalPages: Int)

    private fun getResponsePagination(response: Response<AiringAnimeQuery.Data>): AnilistPagination {
        return AnilistPagination(
            totalItems = response.data?.page?.pageInfo?.total ?: 0,
            currentPage = response.data?.page?.pageInfo?.currentPage ?: 0,
            perPage = response.data?.page?.pageInfo?.perPage ?: 0,
            totalPages = response.data?.page?.pageInfo?.lastPage ?: 0
        )
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AiringAnime> {
        val page = params.key ?: ANILIST_STARTING_PAGE_INDEX
        return try {
            val response = service.getAiringAnimeList(page, params.loadSize)

            getResponseRateLimit(response)
            val pagination = getResponsePagination(response)
            Log.d(
                LOG_TAG,
                """
                    Anime list size: ${pagination.totalItems},
                    current page: ${pagination.currentPage},
                    items per page: ${pagination.perPage},
                    total pages: ${pagination.totalPages}
                """.trimIndent()
            )

            ////////////////////////////////////////////////////////////////////////////////////////
            // TODO: move season calc to separate function and finally to Worker to avoid rate limit
            val serverAnimeList = response.data?.page?.media?.filterNotNull() ?: emptyList()
            val animeList = serverAnimeList.map { medium -> mediumToAiringAnime(medium) }

            val ids = animeList.associateBy({ it.id?.toInt() ?: -1 }, { mutableListOf(it.id?.toInt() ?: -1) })
            searchPrequels(ids)
            ids.forEach { entry ->
                val seasonCount = entry.value.size - 1

                val medium = serverAnimeList.find { medium -> medium.id == entry.key }
                Log.d(LOG_TAG, "Id=${entry.key} name '${medium?.title?.romaji}' season count: $seasonCount")

                val anime = animeList.find { anime -> anime.id == entry.key.toLong() }
                anime?.season = seasonCount
            }
            ////////////////////////////////////////////////////////////////////////////////////////

            LoadResult.Page(
                data = animeList,
                prevKey = if (page == ANILIST_STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (page == response.data?.page?.pageInfo?.lastPage) null else page + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, AiringAnime>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}
