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

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import name.eraxillan.airinganimeschedule.api.AnilistApi
import name.eraxillan.airinganimeschedule.db.AiringAnimeDao
import name.eraxillan.airinganimeschedule.db.AiringAnimeDatabase
import name.eraxillan.airinganimeschedule.db.FavoriteAnimeDao
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.model.FavoriteAnime
import name.eraxillan.airinganimeschedule.utilities.NETWORK_PAGE_SIZE

/**
 * Repository class that works with local and remote data sources
 */
class AiringAnimeRepo(context: Context) {
    companion object {
        private const val LOG_TAG = "54BE6C87_Repository"
    }

    private var database = AiringAnimeDatabase.getInstance(context)
    private var airingDao: AiringAnimeDao = database.airingDao()
    private var favoriteDao: FavoriteAnimeDao = database.favoriteDao()
    private val backend: AnilistApi = AnilistApi.create(AnilistApi.createClient())

    // Favorite anime list local database API
    suspend fun addAnimeToFavorite(anime: AiringAnime): Long {
        return favoriteDao.addAnimeToFavorite(FavoriteAnime(anilistId = anime.anilistId))
    }

    suspend fun deleteFavoriteAnime(anime: AiringAnime) {
        favoriteDao.deleteFavoriteAnime(FavoriteAnime(anilistId = anime.anilistId))
    }

    fun isAnimeAddedToFavorite(anilistId: Long) = favoriteDao.isAnimeAddedToFavorite(anilistId)

    val favoriteAnimeList: LiveData<List<AiringAnime>>
        get() {
            return favoriteDao.getFavoriteAnimeList()
        }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get currently airing anime list, exposed as a stream of data that will emit
     * every time we get more data from the network
     */
    fun getAiringAnimeListStream(): Flow<PagingData<AiringAnime>> {
        Log.d(LOG_TAG, "Query currently airing anime list from backend...")

        val animePagingSourceFactory = { airingDao.getAiringAnimeListPages() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = NETWORK_PAGE_SIZE),
            remoteMediator = AnilistRemoteMediator(database, backend),
            pagingSourceFactory = animePagingSourceFactory
        ).flow
    }
}
