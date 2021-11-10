package name.eraxillan.anilistapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/** A genre of the media */
@Entity(tableName = "media_genres")
@Parcelize
data class MediaGenre constructor(
    /** The id of the genre */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "genre_id")
    val genreId: Long = 0,

    /** The name of the genre */
    @ColumnInfo(name = "name", defaultValue = "")
    val name: String = "",
) : Parcelable
