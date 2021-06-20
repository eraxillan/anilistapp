package name.eraxillan.airinganimeschedule.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import name.eraxillan.airinganimeschedule.model.AiringAnime

/**
 * Airing anime database CRUD: create-read-update-delete
 *
 * LiveData objects can be observed by another object.
 * LiveData notifies any observers when the data changes.
 * This provides a great way to keep user interface elements up
 * to date when items change in the database.
 * LiveData objects do their work in a background thread. By default, Room won’t
 * allow you to make calls to DAO methods on the main thread. By returning LiveData
 * objects, your method becomes an asynchronous query, and there is no restriction to
 * calling it from the main thread.
 */
@Dao
interface AiringAnimeDao {
    @Query("SELECT * FROM airing_animes")
    fun getAiringAnimeList(): LiveData<List<AiringAnime>>

    @Query("SELECT EXISTS(SELECT 1 FROM airing_animes WHERE id = :id LIMIT 1)")
    fun isAddedToFavorite(id: Long?): LiveData<Boolean>

    @Query("SELECT * FROM airing_animes WHERE id = :animeId")
    fun getAiringAnime(animeId: Long): AiringAnime

    // Asynchronous version
    @Query("SELECT * FROM airing_animes WHERE id = :animeId")
    fun getLiveAiringAnime(animeId: Long): LiveData<AiringAnime>

    @Insert(onConflict = REPLACE)
    suspend fun insertAiringAnime(anime: AiringAnime): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(animeList: List<AiringAnime>)

    @Update(onConflict = REPLACE)
    suspend fun updateAiringAnime(anime: AiringAnime)

    @Delete
    suspend fun deleteAiringAnime(anime: AiringAnime)
}
