package name.eraxillan.ongoingschedule.model

import androidx.room.Entity
import androidx.room.PrimaryKey
//import java.time.Year
//import java.net.URL

enum class OngoingType { TV, OVA, ONA }

/*
 https://en.wikipedia.org/wiki/Motion_Picture_Association_film_rating_system
 https://www.kinopoisk.ru/mpaa/

enum class MpaaRating {
    G,     // General audiences: all ages admitted
    PG,    // Parental guidance suggested: some material may not be suitable for children
    PG_13, // Parents strongly cautioned: some material may be inappropriate for children under 13
    R,     // Restricted: under 17 requires accompanying parent or adult guardian
    NC_17  // Adults only: no one 17 and under admitted
}
 */

// FIXME: add Kotlin documentation comments
/**
 *
 */
@Entity
data class Ongoing constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,             // Database primary key
    // TODO: can be `java.net.URL` used instead?
    var url: String = "",             // Wakanim/Crunchyroll URL
    var season: Int = -1,             // Season number
    var originalName: String = "",    // Anime original Japanese name in Romaji
    // TODO: can be `OngoingType` used instead?
    var type: Int = -1,               //
    // TODO: can be `java.time.Year` used instead?
    var year: Int = -1,               //
    var latestEpisode: Int = -1,      // 5, i.e. 5th from 12
    var totalEpisodes: Int = -1,      // 12
    var nextEpisodeDate: Long = -1,   // timestamp
    // FIXME: cause Room compile error due to unsupported `List<String>` type
//    var audioTracks: List<String> = emptyList(),    // "en", "ru"
//    var audioSubtitles: List<String> = emptyList(), // "en", "ru"
    var minAge: Int = -1,             // 16, 18
)
