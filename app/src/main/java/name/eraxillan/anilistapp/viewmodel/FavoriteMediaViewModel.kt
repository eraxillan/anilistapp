package name.eraxillan.anilistapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import name.eraxillan.anilistapp.model.Media
import name.eraxillan.anilistapp.repository.FavoriteMediaRepository
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class FavoriteMediaViewModel @Inject constructor(
    private val favoriteMediaRepository: FavoriteMediaRepository,
) : ViewModel() {

    private var favoriteMediaList: LiveData<List<Media>>? = null

    fun addMediaToFavorite(
        media: Media,
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

    fun deleteFavoriteMedia(media: Media) {
        /*val job =*/ viewModelScope.launch {
            favoriteMediaRepository.deleteFavoriteMedia(media)
        }
        //job.cancelAndJoin()
    }

    fun isMediaAddedToFavorite(anilistId: Long) =
        favoriteMediaRepository.isMediaAddedToFavorite(anilistId)

    fun getFavoriteMediaList(): LiveData<List<Media>>? {
        if (favoriteMediaList == null) {
            /*val job =*/ viewModelScope.launch {
                favoriteMediaList = favoriteMediaRepository.favoriteMediaList
            }
        }
        return favoriteMediaList
    }
}
