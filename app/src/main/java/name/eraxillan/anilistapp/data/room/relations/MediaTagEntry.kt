package name.eraxillan.anilistapp.data.room.relations

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.parcelize.Parcelize
import name.eraxillan.anilistapp.data.room.LocalMedia
import name.eraxillan.anilistapp.model.MediaTag

/** Media and media tag junction table to decompose many-to-many relation */
@Entity(
    tableName = "media_tag_entries",
    primaryKeys = ["media_id", "tag_id"],
    foreignKeys = [
        ForeignKey(
            entity = LocalMedia::class,
            parentColumns = ["anilist_id"],
            childColumns = ["media_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
            // TODO: need testing
            //deferred = true
        ),
        ForeignKey(
            entity = MediaTag::class,
            parentColumns = ["tag_id"],
            childColumns = ["tag_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
            // TODO: need testing
            //deferred = true
        ),
    ],
    // TODO: need testing
    indices = [
        Index("media_id", "tag_id", unique = true),
        Index("media_id"),
        Index("tag_id")
    ]
)
@Parcelize
data class MediaTagEntry(
    //@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "media_id") val mediaId: Long = -1,
    @ColumnInfo(name = "tag_id") val tagId: Long = -1,
) : Parcelable
