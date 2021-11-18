package name.eraxillan.anilistapp.data

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import name.eraxillan.anilistapp.model.MediaSource


@Dao
abstract class MediaSourceDao {
    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(entity: MediaSource): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(vararg entity: MediaSource): List<Long>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(entities: Collection<MediaSource>): List<Long>

    @Update(onConflict = REPLACE)
    abstract suspend fun update(entity: MediaSource)

    @Delete
    abstract suspend fun delete(entity: MediaSource): Int
}
