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

package name.eraxillan.anilistapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import name.eraxillan.anilistapp.data.room.LocalMediaWithRelations
import name.eraxillan.anilistapp.repository.FavoriteMediaRepository
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class FavoriteMediaViewModel @Inject constructor(
    /*private val state: SavedStateHandle,*/
    private val favoriteMediaRepository: FavoriteMediaRepository,
) : ViewModel() {

    private var favoriteMediaList: LiveData<List<LocalMediaWithRelations>>? = null

    fun addMediaToFavorite(
        media: LocalMediaWithRelations,
        @Suppress("UNUSED_PARAMETER") navController: NavController
    ) {
        /*val job =*/ viewModelScope.launch {
            // Save media to database
            val newId = favoriteMediaRepository.addMediaToFavorite(media)
            Timber.i("New media with id=$newId added to the SQLite database")

            // TODO: open `Favorites` fragment?
            /*withContext(Dispatchers.Main) {
                showMediaInfo(media, navController)
            }*/
        }
        //job.cancelAndJoin()
    }

    fun deleteFavoriteMedia(media: LocalMediaWithRelations) {
        /*val job =*/ viewModelScope.launch {
            favoriteMediaRepository.deleteFavoriteMedia(media)
        }
        //job.cancelAndJoin()
    }

    fun isMediaAddedToFavorite(anilistId: Long) =
        favoriteMediaRepository.isMediaAddedToFavorite(anilistId)

    fun getFavoriteMediaList(): LiveData<List<LocalMediaWithRelations>>? {
        if (favoriteMediaList == null) {
            /*val job =*/ viewModelScope.launch {
                favoriteMediaList = favoriteMediaRepository.favoriteMediaList
            }
        }
        return favoriteMediaList
    }
}
