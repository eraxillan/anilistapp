package name.eraxillan.anilistapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/** A tag that describes a theme or element of the media */
@Entity(tableName = "media_tags")
@Parcelize
data class MediaTag constructor(
    /** The id of the tag */
    @PrimaryKey @ColumnInfo(name = "tag_id")
    val tagId: Long = -1,

    /** The name of the tag */
    @ColumnInfo(name = "name", defaultValue = "")
    val name: String = "",

    /** A general description of the tag */
    @ColumnInfo(name = "description", defaultValue = "")
    val description: String = "",

    /** The categories of tags this tag belongs to */
    @ColumnInfo(name = "category", defaultValue = "")
    val category: String = "",

    /** The relevance ranking of the tag out of the 100 for this media */
    @ColumnInfo(name = "rank", defaultValue = "-1")
    val rank: Int = -1,

    /** If the tag could be a spoiler for any media */
    @ColumnInfo(name = "is_general_spoiler", defaultValue = "0")
    val isGeneralSpoiler: Boolean = false,

    /** If the tag is a spoiler for this media */
    @ColumnInfo(name = "is_media_spoiler", defaultValue = "0")
    val isMediaSpoiler: Boolean = false,

    /** If the tag is only for adult 18+ media */
    @ColumnInfo(name = "is_adult", defaultValue = "0")
    val isAdult: Boolean = false,
) : Parcelable
