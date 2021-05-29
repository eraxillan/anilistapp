package name.eraxillan.airinganimeschedule.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
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
interface OngoingDao {
    @Query("SELECT * FROM airing_animes")
    fun loadAll(): LiveData<List<AiringAnime>>

    @Query("SELECT * FROM airing_animes WHERE id = :animeId")
    fun loadOngoing(animeId: Long): AiringAnime

    // Asynchronous version
    @Query("SELECT * FROM airing_animes WHERE id = :animeId")
    fun loadLiveOngoing(animeId: Long): LiveData<AiringAnime>

    @Insert(onConflict = IGNORE)
    fun insertOngoing(anime: AiringAnime): Long

    @Update(onConflict = REPLACE)
    fun updateOngoing(anime: AiringAnime)

    @Delete
    fun deleteOngoing(anime: AiringAnime)
}
