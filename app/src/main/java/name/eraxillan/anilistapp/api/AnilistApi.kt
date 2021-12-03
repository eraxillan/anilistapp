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
import com.apollographql.apollo.api.*
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.http.OkHttpExecutionContext
import kotlinx.coroutines.delay
import name.eraxillan.anilistapp.*
import name.eraxillan.anilistapp.model.RemoteMedia
import name.eraxillan.anilistapp.model.MediaFilter
import name.eraxillan.anilistapp.utilities.NETWORK_REQUEST_RETRY_COUNT
import name.eraxillan.anilistapp.utilities.NETWORK_REQUEST_RETRY_INTERVAL_MS
import name.eraxillan.anilistapp.model.MediaSort as MediaSort
import name.eraxillan.anilistapp.type.MediaRelation as AnilistMediaRelation
import name.eraxillan.anilistapp.type.MediaSort as AnilistMediaSort
import name.eraxillan.anilistapp.type.MediaStatus as AnilistMediaStatus
import name.eraxillan.anilistapp.type.MediaType as AnilistMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.lang.NumberFormatException


/**
 * Used to connect to the Anilist API to fetch media schedule and details
 */
class AnilistApi(private val client: ApolloClient) {

    /*suspend fun getGenreList(): Response<MediaGenresQuery.Data> = postRequestInternal(MediaGenresQuery(), 0)*/

    /*suspend fun getTagList(): Response<MediaTagsQuery.Data> = postRequestInternal(MediaTagsQuery(), 0)*/

    // TODO: Anilist API does not have such kind of query now, but it can be added in future
    //suspend fun getStudioList(): Response<MediaStudiosQuery.Data>? = postRequestInternal(MediaStudiosQuery(), 0)
    /*suspend fun findStudio(name: String): Response<MediaStudioQuery.Data> = postRequestInternal(MediaStudioQuery(name), 0)*/

    suspend fun getMediaList(page: Int, perPage: Int, filter: MediaFilter, sort: MediaSort)
        : Response<MediaListQuery.Data> {

        check(page >= 1) { Timber.e("page < 0") }
        check(perPage >= 1) { Timber.e("perPage < 0") }
        check(sort != MediaSort.UNKNOWN) { Timber.e("sortBy == MediaSort.UNKNOWN") }

        val formats = filter.formats?.mapNotNull { mediaFormat ->
            convertMediaFormat(mediaFormat)
        }.toInput()

        val sortAnilist = convertMediaSortType(sort) ?: AnilistMediaSort.POPULARITY_DESC
        check(sortAnilist != AnilistMediaSort.UNKNOWN__) {
            Timber.e("sortByAnilist == AnilistMediaSort.UNKNOWN__")
        }

        val mediaListQuery = MediaListQuery(
            page = page,
            perPage = perPage,

            genres = filter.genres.toInput(),
            tags = filter.tags.toInput(),
            formats = formats,
            seasonYear = filter.year.toInput(),
            licensedBy = filter.services.toInput(),
            sources = filter.sources?.mapNotNull { source -> convertMediaSource(source) }.toInput(),
            season = convertMediaSeason(filter.season).toInput(),
            status = convertMediaStatus(filter.status).toInput(),
            country = convertMediaCountry(filter.country).toInput(),
            isLicensed = filter.isLicensed.toInput(),

            sort = listOf(sortAnilist).toInput()
        )

        return postRequestInternal(mediaListQuery, 0)
    }

    private suspend fun <Q : Query<R, R, Operation.Variables>, R : Operation.Data>
            postRequestInternal(query: Q, retryNumber: Int): Response<R> {

        return try {
            val response = client.query(query).await()
            if (response.hasErrors()) {
                val messages = response.errors?.joinToString(", ") { error -> error.message }
                throw ApolloException("GraphQL error: $messages")
            }

            val rateLimit = getResponseRateLimit(response)
            Timber.d(
                "Network query rate limit status: ${rateLimit.remaining} from ${rateLimit.total}"
            )

            response
        } catch (e: ApolloException) {
            // Handle protocol errors
            Timber.e("Apollo exception: %s", e.message)

            // Retry on rate limit exceed error (HTTP code 429)
            // TODO: an unreliable way to get HTTP result code, find another one
            if (e.message?.trim() == "HTTP 429") {
                Timber.e("Rate limit exceeded!")
                if (retryNumber > NETWORK_REQUEST_RETRY_COUNT) {
                    Timber.e("$NETWORK_REQUEST_RETRY_COUNT attempts to reset rate limit failed! Abort")
                    throw e
                }

                Timber.d("Sleep ${NETWORK_REQUEST_RETRY_INTERVAL_MS / 1_000} seconds and try again...")
                delay(NETWORK_REQUEST_RETRY_INTERVAL_MS)

                return postRequestInternal(query, retryNumber + 1)
            }

            throw e
        }
    }

    /*suspend fun getMediaDetail(id: Int, page: Int, perPage: Int): Response<MediaDetailQuery.Data> {
        val mediaDetailQuery = MediaDetailQuery(
            page = page,
            perPage = perPage,
            id = Input.fromNullable(id),
            //search = Input.fromNullable("media_name")
        )

        return postRequestInternal(mediaDetailQuery, 0)
    }*/

    private suspend fun getMediaRelations(ids: List<Long>, page: Int, perPage: Int)
        : Response<MediaRelationsQuery.Data> {

        val mediaRelationsQuery = MediaRelationsQuery(
            page = page,
            perPage = perPage,
            id_in = ids.map { it.toInt() }
        )

        return postRequestInternal(mediaRelationsQuery, 0)
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private fun getPrequel(medium: MediaRelationsQuery.Medium): Pair<Boolean, Int> {
        val prequelEdge = medium.relations?.edges?.filterNotNull()?.find { edge ->
            // Media can have only one prequel and have not at all
            if (edge.relationType == AnilistMediaRelation.PREQUEL &&
                edge.node?.type == AnilistMediaType.ANIME &&
                edge.node.status == AnilistMediaStatus.FINISHED
            ) {
                val title = edge.node.title?.romaji
                Timber.d("Prequel media found: $title")
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
        // To achieve this, query all media relations in one request using `id_in` argument

        if (relations.isEmpty()) return 0

        // Recursion stop condition: all media in list marked with special stop-value (-1)
        val finishedCount = relations.count { entry -> entry.value.last() == -1L }
        Timber.d("Relations search progress: $finishedCount from ${relations.size}")
        if (finishedCount == relations.size) return 0

        val ids = relations.map { entry -> entry.value.last() }.filter { entry -> entry != -1L }
        Timber.d("Remaining media ids: $ids")

        val responseInner = getMediaRelations(ids = ids, page = 1, perPage = 30)

        val rateLimit = getResponseRateLimit(responseInner)
        Timber.d(
            "Network query rate limit status: ${rateLimit.remaining} from ${rateLimit.total}"
        )

        delay(100)

        if (responseInner.hasErrors()) {
            Timber.e("Relations request failed: `${responseInner.errors.toString()}`!")
            return -1
        }
        Timber.d("Relations response succeed: ${responseInner.data?.page?.media?.size} media found")
        if (responseInner.data?.page?.media?.isEmpty() == true) return -1

        val mediaList = responseInner.data?.page?.media?.filterNotNull() ?: emptyList()
        mediaList.forEach { medium ->
            Timber.d("Calculation episode count for media '${medium.title?.romaji}'...")

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
        serverMediaList: List<MediaListQuery.Medium>, mediaList: List<RemoteMedia>) {
        val ids = mediaList.associateBy(
            { it.anilistId }, { mutableListOf(it.anilistId) }
        )
        searchPrequels(ids)
        ids.forEach { entry ->
            val seasonCount = entry.value.size - 1

            val medium = serverMediaList.find { medium -> medium.id.toLong() == entry.key }
            Timber.d(
                "Id=${entry.key} name '${medium?.title?.romaji}' season count: $seasonCount"
            )

            val media = mediaList.find { media -> media.anilistId == entry.key }
            media?.season = seasonCount
        }
    }

    data class AnilistRateLimit(val total: Int, val remaining: Int)

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

    fun getResponsePagination(response: Response<MediaListQuery.Data>): AnilistPagination {
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
