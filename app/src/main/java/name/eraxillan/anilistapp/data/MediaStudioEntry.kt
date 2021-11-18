package name.eraxillan.anilistapp.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.parcelize.Parcelize
import name.eraxillan.anilistapp.model.Media
import name.eraxillan.anilistapp.model.MediaStudio

/** Media and media studio junction table to decompose many-to-many relation */
@Entity(
    tableName = "media_studio_entries",
    primaryKeys = ["media_id", "studio_id"],
    foreignKeys = [
        ForeignKey(
            entity = Media::class,
            parentColumns = ["anilist_id"],
            childColumns = ["media_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
            // TODO: need testing
            //deferred = true
        ),
        ForeignKey(
            entity = MediaStudio::class,
            parentColumns = ["studio_id"],
            childColumns = ["studio_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
            // TODO: need testing
            //deferred = true
        ),
    ],
    // TODO: need testing
    indices = [
        Index("media_id", "studio_id", unique = true),
        Index("media_id"),
        Index("studio_id")
    ]
)
@Parcelize
data class MediaStudioEntry(
    //@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "media_id") val mediaId: Long = -1,
    @ColumnInfo(name = "studio_id") val studioId: Long = -1,
) : Parcelable
