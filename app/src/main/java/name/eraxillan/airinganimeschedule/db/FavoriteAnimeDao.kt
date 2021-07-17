package name.eraxillan.airinganimeschedule.db

import androidx.lifecycle.LiveData
import androidx.room.*
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.model.FavoriteAnime

@Dao
interface FavoriteAnimeDao {
    @Query("SELECT * from airing_animes WHERE anilistId IN (SELECT anilistId FROM favorite_animes)")
    fun getFavoriteAnimeList(): LiveData<List<AiringAnime>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAnimeToFavorite(fav: FavoriteAnime): Long

    @Delete
    suspend fun deleteFavoriteAnime(fav: FavoriteAnime)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_animes WHERE anilistId = :anilistId LIMIT 1)")
    fun isAnimeAddedToFavorite(anilistId: Long): LiveData<Boolean>
}
