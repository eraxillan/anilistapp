package name.eraxillan.anilistapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/** An alternative title of the media */
@Entity(
    tableName = "media_title_synonyms",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Media::class,
            parentColumns = ["anilist_id"],
            childColumns = ["media_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
            // TODO: need testing
            //deferred = true
        ),
    ],
    // TODO: need testing
    indices = [
        androidx.room.Index("media_id", unique = false),
    ],
)
@Parcelize
data class MediaTitleSynonym constructor(
    /** The id of the title synonym */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "title_synonym_id")
    val titleSynonymId: Long = 0,

    /** The name of the title synonym */
    @ColumnInfo(name = "name", defaultValue = "")
    val name: String = "",

    /** One-to-many relation foreign key: points to associated media */
    @ColumnInfo(name = "media_id", defaultValue = "-1")
    val mediaId: Long = -1,
) : Parcelable {

    constructor(other: MediaTitleSynonym, mediaId: Long) : this(
        titleSynonymId = other.titleSynonymId,
        name = other.name,

        mediaId = mediaId,
    )
}