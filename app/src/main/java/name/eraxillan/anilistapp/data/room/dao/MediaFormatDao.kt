package name.eraxillan.anilistapp.data.room.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import name.eraxillan.anilistapp.model.MediaFormat


@Dao
abstract class MediaFormatDao {
    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(entity: MediaFormat): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(vararg entity: MediaFormat): List<Long>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(entities: Collection<MediaFormat>): List<Long>

    @Update(onConflict = REPLACE)
    abstract suspend fun update(entity: MediaFormat)

    @Delete
    abstract suspend fun delete(entity: MediaFormat): Int
}
