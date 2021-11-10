package name.eraxillan.anilistapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "media_formats")
@Parcelize
data class MediaFormat constructor(
    @PrimaryKey @ColumnInfo(name = "format_id")
    val formatId: Long = -1,

    @ColumnInfo(name = "format")
    val format: MediaFormatEnum = MediaFormatEnum.UNKNOWN,
) : Parcelable
