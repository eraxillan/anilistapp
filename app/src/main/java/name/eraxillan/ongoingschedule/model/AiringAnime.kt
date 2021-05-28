package name.eraxillan.ongoingschedule.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import name.eraxillan.ongoingschedule.db.ZonedScheduledTime
import java.net.URL
import java.time.LocalDate


// Just for brevity in this module
private typealias DMY = LocalDate
private typealias ZST = ZonedScheduledTime

// FIXME: implement custom App class and replace with string resource
private val INVALID_URL = URL("https://www.invalid.com")


/**
 * An anime description
 */
@Entity(tableName = "airing_animes")
@Parcelize
data class AiringAnime constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,             // Database primary key
    var url: URL = INVALID_URL,       // Anilist/MyAnimeList/Wakanim/Crunchyroll/etc. URL
    var season: Int = -1,             // Season number
    var originalName: String = "",    // Original Japanese name in Romaji
    var latestEpisode: Int = -1,      // Latest episode number: e.g. 5, i.e. 5th from 12
    var totalEpisodes: Int = -1,      // Total episode count: e.g. 12
    var releaseDate: DMY? = null,     // Aired day+month+year
    var nextEpisodeDate: ZST? = null, // Latest episode launch day+time with timezone
    var minAge: Int = -1,             // Minimal accepted age: e.g. 16 or 18
) : Parcelable {

    fun copyDataFields(other: AiringAnime) {
        // Ignore `id` and url `fields`, other filled by parser
        season = other.season
        originalName = other.originalName
        latestEpisode = other.latestEpisode
        totalEpisodes = other.totalEpisodes
        releaseDate = other.releaseDate
        nextEpisodeDate = other.nextEpisodeDate
        minAge = other.minAge
    }
}
