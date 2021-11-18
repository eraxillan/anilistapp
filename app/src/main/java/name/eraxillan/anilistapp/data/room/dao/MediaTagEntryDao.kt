package name.eraxillan.anilistapp.data.room.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import name.eraxillan.anilistapp.data.MediaTagEntry

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
