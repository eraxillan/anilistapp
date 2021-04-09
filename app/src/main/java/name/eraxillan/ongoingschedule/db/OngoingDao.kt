package name.eraxillan.ongoingschedule.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import name.eraxillan.ongoingschedule.model.Ongoing

/**
 * Ongoing database CRUD: create-read-update-delete
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
    @Query("SELECT * FROM Ongoing")
    fun loadAll(): LiveData<List<Ongoing>>

    @Query("SELECT * FROM Ongoing WHERE id = :ongoingId")
    fun loadOngoing(ongoingId: Long): Ongoing

    // Asynchronous version
    @Query("SELECT * FROM Ongoing WHERE id = :ongoingId")
    fun loadLiveOngoing(ongoingId: Long): LiveData<Ongoing>

    @Insert(onConflict = IGNORE)
    fun insertOngoing(ongoing: Ongoing): Long

    @Update(onConflict = REPLACE)
    fun updateOngoing(ongoing: Ongoing)

    @Delete
    fun deleteOngoing(ongoing: Ongoing)
}
