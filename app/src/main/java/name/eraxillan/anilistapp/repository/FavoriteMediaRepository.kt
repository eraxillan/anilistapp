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
