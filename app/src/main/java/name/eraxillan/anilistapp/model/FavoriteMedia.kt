package name.eraxillan.anilistapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/** Local favorite media data */
@Entity(tableName = "favorite_media_collection")
@Parcelize
data class FavoriteMedia constructor(
    /** The unique identifier ("id") of the media, also serve as a SQLite primary key */
    @PrimaryKey @ColumnInfo(name = "anilist_id")
    val anilistId: Long = -1,
) : Parcelable
