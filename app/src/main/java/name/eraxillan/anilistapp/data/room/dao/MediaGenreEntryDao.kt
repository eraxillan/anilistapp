package name.eraxillan.anilistapp.data.room.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import name.eraxillan.anilistapp.data.MediaGenreEntry


@Dao
abstract class MediaGenreEntryDao {
    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(entity: MediaGenreEntry): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(vararg entity: MediaGenreEntry): List<Long>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertAll(entities: Collection<MediaGenreEntry>): List<Long>

    @Update(onConflict = REPLACE)
    abstract suspend fun update(entity: MediaGenreEntry)

    @Delete
    abstract suspend fun delete(entity: MediaGenreEntry): Int

    @Query("DELETE FROM media_genre_entries")
    abstract suspend fun deleteAll()
}
