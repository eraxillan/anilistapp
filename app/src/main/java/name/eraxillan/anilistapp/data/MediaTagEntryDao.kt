package name.eraxillan.anilistapp.data

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
abstract class MediaTagEntryDao {
    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(entity: MediaTagEntry): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(vararg entity: MediaTagEntry): List<Long>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(entities: Collection<MediaTagEntry>): List<Long>

    @Update(onConflict = REPLACE)
    abstract suspend fun update(entity: MediaTagEntry)

    @Delete
    abstract suspend fun delete(entity: MediaTagEntry): Int

    @Query("DELETE FROM media_tag_entries")
    abstract suspend fun deleteAll()
}
