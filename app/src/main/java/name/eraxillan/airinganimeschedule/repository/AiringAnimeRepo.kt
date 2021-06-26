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
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import name.eraxillan.airinganimeschedule.api.AnilistApi
import name.eraxillan.airinganimeschedule.db.AiringAnimeDao
import name.eraxillan.airinganimeschedule.db.AiringAnimeDatabase
import name.eraxillan.airinganimeschedule.db.AnilistPagingSource
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.utilities.NETWORK_PAGE_SIZE

/**
 * Repository pattern implementation: make an independent from concrete data source wrapper
 */
class AiringAnimeRepo(context: Context) {
    private var db = AiringAnimeDatabase.getInstance(context)
    private var airingAnimeDao: AiringAnimeDao = db.airingAnimeDao()
    private val service: AnilistApi = AnilistApi.create(AnilistApi.createClient())

    // Favorite anime list local database API
    suspend fun addAiringAnime(anime: AiringAnime): Long {
        val newId = airingAnimeDao.insertAiringAnime(anime)
        anime.id = newId
        return newId
    }

    suspend fun deleteAiringAnime(anime: AiringAnime) {
        airingAnimeDao.deleteAiringAnime(anime)
    }

    val airingAnimeList: LiveData<List<AiringAnime>>
        get() {
            return airingAnimeDao.getAiringAnimeList()
        }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Currently airing anime list remote Anilist API
    val remoteAiringAnimeList: LiveData<PagingData<AiringAnime>>
        get() {
            return Pager(
                config = PagingConfig(enablePlaceholders = false, pageSize = NETWORK_PAGE_SIZE),
                pagingSourceFactory = { AnilistPagingSource(service) }
            ).liveData
        }

    fun isAddedToFavorite(id: Int) = airingAnimeDao.isAddedToFavorite(id.toLong())
}
