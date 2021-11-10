package name.eraxillan.anilistapp.db

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE


@Dao
abstract class MediaStudioEntryDao {
    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(entity: MediaStudioEntry): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(vararg entity: MediaStudioEntry): List<Long>

    @Insert(onConflict = IGNORE)
    abstract suspend fun insertAll(entities: Collection<MediaStudioEntry>): List<Long>

    @Update(onConflict = REPLACE)
    abstract suspend fun update(entity: MediaStudioEntry)

    @Delete
    abstract suspend fun delete(entity: MediaStudioEntry): Int

    @Query("DELETE FROM media_studio_entries")
    abstract suspend fun deleteAll()
}
