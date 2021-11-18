package name.eraxillan.anilistapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import name.eraxillan.anilistapp.model.FavoriteMedia

@Dao
interface FavoriteMediaDao {
    @Transaction
    @Query("SELECT * FROM media_collection WHERE anilist_id IN (SELECT anilist_id FROM favorite_media_collection)")
    fun getFavoriteMediaList(): LiveData<List<LocalMedia>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMediaToFavorite(media: FavoriteMedia): Long

    @Delete
    suspend fun deleteFavoriteMedia(media: FavoriteMedia)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_media_collection WHERE anilist_id = :anilistId LIMIT 1)")
    fun isMediaAddedToFavorite(anilistId: Long): LiveData<Boolean>
}
