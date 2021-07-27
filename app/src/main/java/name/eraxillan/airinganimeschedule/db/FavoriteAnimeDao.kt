package name.eraxillan.airinganimeschedule.db

import androidx.lifecycle.LiveData
import androidx.room.*
import name.eraxillan.airinganimeschedule.model.Media
import name.eraxillan.airinganimeschedule.model.FavoriteMedia

@Dao
interface FavoriteAnimeDao {
    @Query("SELECT * FROM media_collection WHERE anilistId IN (SELECT anilistId FROM favorite_media_collection)")
    fun getFavoriteAnimeList(): LiveData<List<Media>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAnimeToFavorite(fav: FavoriteMedia): Long

    @Delete
    suspend fun deleteFavoriteAnime(fav: FavoriteMedia)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_media_collection WHERE anilistId = :anilistId LIMIT 1)")
    fun isAnimeAddedToFavorite(anilistId: Long): LiveData<Boolean>
}
