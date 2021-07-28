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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import name.eraxillan.airinganimeschedule.model.Media
import name.eraxillan.airinganimeschedule.repository.MediaRepo
import timber.log.Timber


class MediaViewModel(application: Application): AndroidViewModel(application) {
    private var repository: MediaRepo = MediaRepo(getApplication())
    private var favoriteMediaList: LiveData<List<Media>>? = null
    private var mediaList: Flow<PagingData<Media>>? = null

    fun addMediaToFavorite(media: Media, navController: NavController) {
        /*val job =*/ viewModelScope.launch {
            // Save media to database
            val newId = repository.addMediaToFavorite(media)
            Timber.i("New media with id=$newId added to the SQLite database")

            // TODO: open `Favorites` fragment?
            /*withContext(Dispatchers.Main) {
                showMediaInfo(media, navController)
            }*/
        }
        //job.cancelAndJoin()
    }

    fun deleteFavoriteMedia(media: Media) {
        /*val job =*/ viewModelScope.launch {
            repository.deleteFavoriteMedia(media)
        }
        //job.cancelAndJoin()
    }

    fun isMediaAddedToFavorite(anilistId: Long) = repository.isMediaAddedToFavorite(anilistId)

    fun getFavoriteMediaList(): LiveData<List<Media>>? {
        if (favoriteMediaList == null) {
            /*val job =*/ viewModelScope.launch {
                favoriteMediaList = repository.favoriteMediaList
            }
        }
        return favoriteMediaList
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun getMediaListStream(): Flow<PagingData<Media>> {
        if (mediaList == null) {
            mediaList = repository
                .getMediaListStream()
                .cachedIn(viewModelScope)
        }
        return mediaList!!
    }
}
