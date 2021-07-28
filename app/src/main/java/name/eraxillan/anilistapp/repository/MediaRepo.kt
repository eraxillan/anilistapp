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

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import name.eraxillan.anilistapp.api.AnilistApi
import name.eraxillan.anilistapp.db.MediaDao
import name.eraxillan.anilistapp.db.MediaDatabase
import name.eraxillan.anilistapp.db.FavoriteMediaDao
import name.eraxillan.anilistapp.model.Media
import name.eraxillan.anilistapp.model.FavoriteMedia
import name.eraxillan.anilistapp.utilities.NETWORK_PAGE_SIZE
import timber.log.Timber

/**
 * Repository class that works with local and remote data sources
 */
class MediaRepo(context: Context) {

    private var database = MediaDatabase.getInstance(context)
    private var mediaDao: MediaDao = database.mediaDao()
    private var favoriteDao: FavoriteMediaDao = database.favoriteDao()
    private val backend: AnilistApi = AnilistApi.create(AnilistApi.createClient())

    // Favorite media list local database API
    suspend fun addMediaToFavorite(media: Media): Long {
        return favoriteDao.addMediaToFavorite(FavoriteMedia(anilistId = media.anilistId))
    }

    suspend fun deleteFavoriteMedia(media: Media) {
        favoriteDao.deleteFavoriteMedia(FavoriteMedia(anilistId = media.anilistId))
    }

    fun isMediaAddedToFavorite(anilistId: Long) = favoriteDao.isMediaAddedToFavorite(anilistId)

    val favoriteMediaList: LiveData<List<Media>>
        get() {
            return favoriteDao.getFavoriteMediaList()
        }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get media list and exposed as a stream of data,
     * that will emit every time we get more data from the network
     */
    fun getMediaListStream(): Flow<PagingData<Media>> {
        Timber.d("Query media list from remote backend...")

        val pagingSourceFactory = { mediaDao.getMediaListPages() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = NETWORK_PAGE_SIZE),
            remoteMediator = AnilistRemoteMediator(database, backend),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}
