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

package name.eraxillan.anilistapp.repository

import androidx.lifecycle.LiveData
import name.eraxillan.anilistapp.data.room.dao.FavoriteMediaDao
import name.eraxillan.anilistapp.model.FavoriteMedia
import name.eraxillan.anilistapp.data.room.LocalMediaWithRelations
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteMediaRepository @Inject constructor(
    private val favoriteDao: FavoriteMediaDao
) {

    // Favorite media list local database API
    suspend fun addMediaToFavorite(media: LocalMediaWithRelations): Long {
        return favoriteDao.addMediaToFavorite(FavoriteMedia(anilistId = media.localMedia.anilistId))
    }

    suspend fun deleteFavoriteMedia(media: LocalMediaWithRelations) {
        favoriteDao.deleteFavoriteMedia(FavoriteMedia(anilistId = media.localMedia.anilistId))
    }

    fun isMediaAddedToFavorite(anilistId: Long) = favoriteDao.isMediaAddedToFavorite(anilistId)

    val favoriteMediaList: LiveData<List<LocalMediaWithRelations>>
        get() {
            return favoriteDao.getFavoriteMediaList()
        }
}
