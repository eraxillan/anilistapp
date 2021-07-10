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

package name.eraxillan.airinganimeschedule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.repository.AiringAnimeRepo


class AiringAnimeViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        private const val LOG_TAG = "54BE6C87_AAVM" // AAVM = AiringAnimeViewModel
    }

    private var repository: AiringAnimeRepo = AiringAnimeRepo(getApplication())
    private var favoriteAnimeList: LiveData<List<AiringAnime>>? = null
    private var airingAnimeList: Flow<PagingData<AiringAnime>>? = null

    fun addAnimeToFavorite(anime: AiringAnime, navController: NavController) {
        /*val job =*/ viewModelScope.launch {
            // Save airing anime to database
            val newId = repository.addAnimeToFavorite(anime)
            Log.i(LOG_TAG, "New anime with id=$newId added to the SQLite database")

            // TODO: open `Favorites` fragment?
            /*withContext(Dispatchers.Main) {
                showAiringAnimeInfo(anime, navController)
            }*/
        }
        //job.cancelAndJoin()
    }

    fun deleteFavoriteAnime(anime: AiringAnime) {
        /*val job =*/ viewModelScope.launch {
            repository.deleteFavoriteAnime(anime)
        }
        //job.cancelAndJoin()
    }

    fun isAnimeAddedToFavorite(anilistId: Int) = repository.isAnimeAddedToFavorite(anilistId)

    fun getFavoriteAnimeList(): LiveData<List<AiringAnime>>? {
        if (favoriteAnimeList == null) {
            /*val job =*/ viewModelScope.launch {
                favoriteAnimeList = repository.favoriteAnimeList
            }
        }
        return favoriteAnimeList
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun getAiringAnimeListStream(): Flow<PagingData<AiringAnime>> {
        if (airingAnimeList == null) {
            airingAnimeList = repository
                .getAiringAnimeListStream()
                .cachedIn(viewModelScope)
        }
        return airingAnimeList!!
    }
}
