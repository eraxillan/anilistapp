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

package name.eraxillan.airinganimeschedule.api

import android.os.Looper
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import name.eraxillan.airinganimeschedule.AiringAnimeQuery
import name.eraxillan.airinganimeschedule.AnimeDetailQuery
import name.eraxillan.airinganimeschedule.AnimeRelationsQuery
import name.eraxillan.airinganimeschedule.type.MediaSeason
import name.eraxillan.airinganimeschedule.type.MediaSort
import name.eraxillan.airinganimeschedule.type.MediaStatus
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Used to connect to the Anilist API to fetch airing anime schedule and details
 */
class AnilistApi(private val client: ApolloClient) {

    suspend fun getAiringAnimeList(page: Int, perPage: Int): Response<AiringAnimeQuery.Data> {
        check(page >= 1)
        check(perPage >= 1)

        val airingAnimeQuery = AiringAnimeQuery(
            page = page,
            perPage = perPage,
            // FIXME: detect current/previous season/year and remove hardcode
            season = MediaSeason.SPRING,
            seasonYear = 2021,
            sort = listOf(MediaSort.POPULARITY_DESC),
            status = MediaStatus.RELEASING
        )

        return client.query(airingAnimeQuery).await()
    }

    suspend fun getAnimeDetail(id: Int, page: Int, perPage: Int): Response<AnimeDetailQuery.Data> {
        val animeDetailQuery = AnimeDetailQuery(
            page = page,
            perPage = perPage,
            id = Input.fromNullable(id),
            //search = Input.fromNullable("anime_name")
        )

        return client.query(animeDetailQuery).await()
    }

    suspend fun getAnimeRelations(ids: List<Int>, page: Int, perPage: Int): Response<AnimeRelationsQuery.Data> {
        val animeRelationsQuery = AnimeRelationsQuery(
            page = page,
            perPage = perPage,
            id_in = ids
        )

        return client.query(animeRelationsQuery).await()
    }

    companion object {
        private const val BASE_URL = "https://graphql.anilist.co"

        fun createClient(): ApolloClient {
            check(Looper.myLooper() == Looper.getMainLooper()) {
                "Only the main thread can get the apolloClient instance!"
            }

            val logger = HttpLoggingInterceptor { Log.d("ANILIST_API", it) }
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
