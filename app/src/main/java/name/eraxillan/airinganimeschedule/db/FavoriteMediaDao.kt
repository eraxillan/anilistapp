package name.eraxillan.airinganimeschedule.db

import androidx.lifecycle.LiveData
import androidx.room.*
import name.eraxillan.airinganimeschedule.model.Media
import name.eraxillan.airinganimeschedule.model.FavoriteMedia

@Dao
interface FavoriteMediaDao {
    @Query("SELECT * FROM media_collection WHERE anilistId IN (SELECT anilistId FROM favorite_media_collection)")
    fun getFavoriteMediaList(): LiveData<List<Media>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMediaToFavorite(media: FavoriteMedia): Long

    @Delete
    suspend fun deleteFavoriteMedia(media: FavoriteMedia)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_media_collection WHERE anilistId = :anilistId LIMIT 1)")
    fun isMediaAddedToFavorite(anilistId: Long): LiveData<Boolean>
}
