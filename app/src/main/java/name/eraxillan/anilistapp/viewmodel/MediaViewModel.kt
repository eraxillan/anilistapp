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

import android.util.LruCache
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import name.eraxillan.anilistapp.db.LocalMedia
import name.eraxillan.anilistapp.model.MediaFilter
import name.eraxillan.anilistapp.model.MediaSort
import name.eraxillan.anilistapp.repository.MediaRepo
import name.eraxillan.anilistapp.utilities.MEDIA_SEARCH_CACHE_SIZE
import timber.log.Timber
import java.util.*
import javax.inject.Inject


@HiltViewModel
class MediaViewModel @Inject constructor(
    private val mediaRepository: MediaRepo,
) : ViewModel() {

    private data class MediaSearchOptions(val filter: MediaFilter, val sort: MediaSort) {
        override fun hashCode(): Int {
            return Objects.hash(filter, sort)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MediaSearchOptions

            if (filter != other.filter) return false
            if (sort != other.sort) return false

            return true
        }
    }
    private val cache = LruCache<Int, Flow<PagingData<LocalMedia>>>(MEDIA_SEARCH_CACHE_SIZE)

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun getMediaListStream(filter: MediaFilter, sortBy: MediaSort): Flow<PagingData<LocalMedia>> {
        val cacheKey = MediaSearchOptions(filter, sortBy).hashCode()
        val cachedResult = cache.get(cacheKey)
        if (cachedResult != null) {
            Timber.d("return cached media list for filter/sort hash=$cacheKey")
            return cachedResult
        }

        val newResult = mediaRepository
            .getMediaListStream(filter, sortBy)
            .cachedIn(viewModelScope)
        cache.put(cacheKey, newResult)
        Timber.d("add to cache media list for filter/sort hash=$cacheKey")

        return newResult
    }
}
