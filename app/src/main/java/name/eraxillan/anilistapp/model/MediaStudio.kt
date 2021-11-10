package name.eraxillan.anilistapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "media_studios")
@Parcelize
data class MediaStudio constructor(
    /** The unique identifier of the studio **/
    @PrimaryKey
    @ColumnInfo(name = "studio_id")
    val studioId: Long = 0,

    /** The name of the studio */
    @ColumnInfo(name = "name", defaultValue = "")
    val name: String = "",

    /** Whether the studio is an animation studio or a different kind of company */
    @ColumnInfo(name = "is_animation_studio", defaultValue = "0")
    val isAnimationStudio: Boolean = false,

    /** The media the studio has worked on */
    /*@Ignore*/
    /*val medias: List<Media> = emptyList(),*/

    /** The URL for the studio page on the AniList website */
    // TODO: use URL class instead of raw `String`
    @ColumnInfo(name = "site_url", defaultValue = "")
    val siteUrl: String = "",

    /** If the studio is marked as favourite by the currently authenticated user */
    /*@ColumnInfo(name = "is_favorite", defaultValue = "0")*/
    /*val isFavorite: Boolean = false,*/

    /** The amount of user's who have added the studio to favorites */
    @ColumnInfo(name = "favorites", defaultValue = "-1")
    val favorites: Int = -1,
) : Parcelable
