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

package name.eraxillan.anilistapp.api

import android.os.Looper
import androidx.core.text.isDigitsOnly
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloExperimental
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.http.OkHttpExecutionContext
import kotlinx.coroutines.delay
import name.eraxillan.anilistapp.AiringAnimeQuery
import name.eraxillan.anilistapp.AnimeRelationsQuery
import name.eraxillan.anilistapp.model.Media
import name.eraxillan.anilistapp.type.MediaRelation
import name.eraxillan.anilistapp.type.MediaSort
import name.eraxillan.anilistapp.type.MediaStatus
import name.eraxillan.anilistapp.type.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.lang.NumberFormatException
import java.time.LocalDate

/**
 * Used to connect to the Anilist API to fetch airing anime schedule and details
 */
class AnilistApi(private val client: ApolloClient) {

    /**
     * Get currently airing anime list, ordered by popularity (must popular first)
     */
    suspend fun getAiringAnimeList(page: Int, perPage: Int): Response<AiringAnimeQuery.Data> {
        check(page >= 1)
        check(perPage >= 1)

        val airingAnimeQuery = AiringAnimeQuery(
            page = page,
            perPage = perPage,
            seasonYear = LocalDate.now().year,
            sort = listOf(MediaSort.POPULARITY_DESC),
            status = MediaStatus.RELEASING
        )

        // TODO: use `client.query(airingAnimeQuery).enqueue(...)` instead?
        return client.query(airingAnimeQuery).await()
    }

    /*suspend fun getAnimeDetail(id: Int, page: Int, perPage: Int): Response<AnimeDetailQuery.Data> {
        val animeDetailQuery = AnimeDetailQuery(
            page = page,
            perPage = perPage,
            id = Input.fromNullable(id),
            //search = Input.fromNullable("anime_name")
        )

        return client.query(animeDetailQuery).await()
    }*/

    private suspend fun getAnimeRelations(ids: List<Long>, page: Int, perPage: Int): Response<AnimeRelationsQuery.Data> {
        val animeRelationsQuery = AnimeRelationsQuery(
            page = page,
            perPage = perPage,
            id_in = ids.map { it.toInt() }
        )

        return client.query(animeRelationsQuery).await()
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private fun getPrequel(medium: AnimeRelationsQuery.Medium): Pair<Boolean, Int> {
        val prequelEdge = medium.relations?.edges?.filterNotNull()?.find { edge ->
            // Anime can have only one prequel and have not at all
            if (edge.relationType == MediaRelation.PREQUEL &&
                edge.node?.type == MediaType.ANIME &&
                edge.node.status == MediaStatus.FINISHED
            ) {
                val title = edge.node.title?.romaji
                Timber.d("Prequel anime found: $title")
                true
            } else
                false
        }

        val prequelId = prequelEdge?.node?.id ?: -1
        return Pair(prequelEdge != null, prequelId)
    }

    private tailrec suspend fun searchPrequels(relations: Map<Long, MutableList<Long>>): Int {
        // Free AniList API rate limit restricted with just 90 req/sec!
        // So we need to make as few requests as possible.
        // To achieve this, query all airing anime relations in one request using `id_in` argument

        if (relations.isEmpty()) return 0

        // Recursion stop condition: all anime in list marked with special stop-value (-1)
        val finishedCount = relations.count { entry -> entry.value.last() == -1L }
        Timber.d("Relations search progress: $finishedCount from ${relations.size}")
        if (finishedCount == relations.size) return 0

        val ids = relations.map { entry -> entry.value.last() }.filter { entry -> entry != -1L }
        Timber.d("Remaining anime ids: ${ids.toString()}")

        val responseInner = getAnimeRelations(ids = ids, page = 1, perPage = 30)

        val rateLimit = getResponseRateLimit(responseInner)
        Timber.d(
            "Network query rate limit status: ${rateLimit.remaining} from ${rateLimit.total}"
        )

        delay(100)

        if (responseInner.hasErrors()) {
            Timber.e("Relations request failed: `${responseInner.errors.toString()}`!")
            return -1
        }
        Timber.d("Relations response succeed: ${responseInner.data?.page?.media?.size} anime found")
        if (responseInner.data?.page?.media?.isEmpty() == true) return -1

        val animeList = responseInner.data?.page?.media?.filterNotNull() ?: emptyList()
        animeList.forEach { medium ->
            Timber.d("Calculation episode count for anime '${medium.title?.romaji}'...")

            // Find key with value
            val parent = relations.entries.find { entry -> entry.value.indexOf(medium.id.toLong()) != -1 }
            val id = parent?.key ?: medium.id
            check(relations.containsKey(id))

            val (hasPrequel, prequelId) = getPrequel(medium)
            if (hasPrequel) {
                relations[id]?.add(prequelId.toLong())
            } else {
                relations[id]?.add(-1)
            }
        }

        return searchPrequels(relations)
    }

    // TODO: move to custom Worker to increase load speed and avoid rate limit
    suspend fun fillEpisodeCount(
        serverAnimeList: List<AiringAnimeQuery.Medium>, animeList: List<Media>) {
        val ids = animeList.associateBy(
            { it.anilistId }, { mutableListOf(it.anilistId) }
        )
        searchPrequels(ids)
        ids.forEach { entry ->
            val seasonCount = entry.value.size - 1

            val medium = serverAnimeList.find { medium -> medium.id.toLong() == entry.key }
            Timber.d(
                "Id=${entry.key} name '${medium?.title?.romaji}' season count: $seasonCount"
            )

            val anime = animeList.find { anime -> anime.anilistId == entry.key }
            anime?.season = seasonCount
        }
    }

    data class AnilistRateLimit(val total: Int, val remaining: Int)

    // FIXME: create a response pool class to handle server query rate limit
    fun <T> getResponseRateLimit(response: Response<T>): AnilistRateLimit {
        val DEFAULT_RATE_LIMIT = 90
        val RATE_LIMIT_TOTAL_KEY = "X-RateLimit-Limit"
        val RATE_LIMIT_REMAINING_KEY = "X-RateLimit-Remaining"

        // See https://anilist.gitbook.io/anilist-apiv2-docs/overview/rate-limiting for details
        @OptIn(ApolloExperimental::class)
        val httpContext = response.executionContext[OkHttpExecutionContext.KEY]
        @OptIn(ApolloExperimental::class)
        val headers = httpContext?.response?.headers

        /*headers?.names()?.forEach { name ->
            Timber.d("Response HTTP header: '${name}' => '${headers.get(name)}'")
        }*/

        val totalStr = headers?.get(RATE_LIMIT_TOTAL_KEY).orEmpty()
        val remainingStr = headers?.get(RATE_LIMIT_REMAINING_KEY).orEmpty()

        val total = try {
            if (totalStr.isEmpty() || !totalStr.isDigitsOnly()) DEFAULT_RATE_LIMIT else totalStr.toInt()
        } catch (exc: NumberFormatException) {
            Timber.e("Invalid total rate limit value: '$totalStr'!")
            DEFAULT_RATE_LIMIT
        }

        val remaining = try {
            if (remainingStr.isEmpty() || !remainingStr.isDigitsOnly()) 0 else remainingStr.toInt()
        } catch (exc: NumberFormatException) {
            Timber.e("Invalid remaining rate limit value: '$remainingStr'!")
            0
        }

        return AnilistRateLimit(total, remaining)
    }

    data class AnilistPagination(
        val totalItems: Int,
        val currentPage: Int,
        val perPage: Int,
        val totalPages: Int,
        val hasNextPage: Boolean
    )

    fun getResponsePagination(response: Response<AiringAnimeQuery.Data>): AnilistPagination {
        return AnilistPagination(
            totalItems = response.data?.page?.pageInfo?.total ?: 0,
            currentPage = response.data?.page?.pageInfo?.currentPage ?: 0,
            perPage = response.data?.page?.pageInfo?.perPage ?: 0,
            totalPages = response.data?.page?.pageInfo?.lastPage ?: 0,
            hasNextPage = response.data?.page?.pageInfo?.hasNextPage ?: false
        )
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private const val BASE_URL = "https://graphql.anilist.co"

        fun createClient(): ApolloClient {
            check(Looper.myLooper() == Looper.getMainLooper()) {
                "Only the main thread can get the apolloClient instance!"
            }

            val logger = HttpLoggingInterceptor { Timber.d(it) }
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .build()
        }

        fun create(client: ApolloClient): AnilistApi {
            return AnilistApi(client)
        }
    }
}
