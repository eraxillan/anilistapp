package name.eraxillan.anilistapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "media_sources")
@Parcelize
data class MediaSource constructor(
    @PrimaryKey @ColumnInfo(name = "source_id")
    val sourceId: Long = -1,

    @ColumnInfo(name = "source")
    val source: MediaSourceEnum = MediaSourceEnum.UNKNOWN,
) : Parcelable
