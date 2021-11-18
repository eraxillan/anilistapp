package name.eraxillan.anilistapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import name.eraxillan.anilistapp.data.FavoriteMediaDao
import name.eraxillan.anilistapp.data.convertLocalMedia
import name.eraxillan.anilistapp.model.FavoriteMedia
import name.eraxillan.anilistapp.model.Media
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteMediaRepository @Inject constructor(
    private val favoriteDao: FavoriteMediaDao
) {

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
            return favoriteDao.getFavoriteMediaList().map { localMediaList ->
                localMediaList.map { localMedia ->
                    convertLocalMedia(localMedia)
                }
            }
        }
}
