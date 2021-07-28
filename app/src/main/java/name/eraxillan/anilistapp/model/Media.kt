/*
 * Copyright 2021 Aleksandr Kamyshnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package name.eraxillan.anilistapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import name.eraxillan.anilistapp.db.ZonedScheduledTime
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime

// FIXME: implement custom App class and replace this with string resource
val INVALID_URL = URL("https://www.invalid.com")

/** The current releasing status of the media */
enum class MediaStatus {
    /** Has completed and is no longer being released */
    FINISHED { override fun toString() = "Finished" },

    /** Currently releasing */
    RELEASING { override fun toString() = "Airing" },

    /** To be released at a later date */
    NOT_YET_RELEASED { override fun toString() = "Not yet aired" },

    /** Ended before the work could be finished */
    CANCELLED { override fun toString() = "Cancelled" },

    /** Is currently paused from releasing and will resume at a later date */
    HIATUS { override fun toString() = "Hiatus" },

    /** Constant for unknown enum values */
    UNKNOWN { override fun toString() = "?" },
}

/** The format the media was released in */
enum class MediaFormat {
    /** Anime broadcast on television */
    TV { override fun toString() = "TV" },

    /** Anime which are under 15 minutes in length and broadcast on television */
    TV_SHORT { override fun toString() = "TV Short" },

    /** Anime movies with a theatrical release */
    MOVIE { override fun toString() = "Movie" },

    /**
     * Special episodes that have been included in DVD/Blu-ray releases,
     * picture dramas, pilots, etc
     */
    SPECIAL { override fun toString() = "Special" },

    /**
     * OVA: Original Video Animation
     *
     * Anime that have been released directly on DVD/Blu-ray without
     * originally going through a theatrical release or television broadcast
     */
    OVA { override fun toString() = "OVA" },

    /**
     * ONA: Original Net Animation
     *
     * Anime that have been originally released online or are only available
     * through streaming services.
     */
    ONA { override fun toString() = "ONA" },

    /** Short anime released as a music video */
    MUSIC { override fun toString() = "Music" },

    /** Professionally published manga with more than one chapter */
    MANGA { override fun toString() = "Manga" },

    /** Written books released as a series of light novels */
    NOVEL { override fun toString() = "Light Novel" },

    /** Manga with just one chapter */
    ONE_SHOT { override fun toString() = "One Shot" },

    /** Constant for unknown enum values */
    UNKNOWN { override fun toString() = "?" },
}

//enum class MediaType { ANIME, MANGA, UNKNOWN }

/** The season the media was initially released in */
enum class MediaSeason {
    /** Months December to February */
    WINTER { override fun toString() = "Winter" },

    /** Months March to May */
    SPRING { override fun toString() = "Spring" },

    /** Months June to August */
    SUMMER { override fun toString() = "Summer" },

    /** Months September to November */
    FALL { override fun toString() = "Fall" },

    /** Constant for unknown enum values */
    UNKNOWN { override fun toString() = "?" },
}

/** Source type the media was adapted from */
enum class MediaSource {
    /** An original production not based of another work */
    ORIGINAL { override fun toString() = "Original" },

    /** Asian comic book */
    MANGA { override fun toString() = "Manga" },

    /** Written work published in volumes */
    LIGHT_NOVEL { override fun toString() = "Light Novel" },

    /** Video game driven primary by text and narrative */
    VISUAL_NOVEL { override fun toString() = "Visual Novel" },

    /** Video game (except visual novel) */
    VIDEO_GAME { override fun toString() = "Video Game" },

    /** Something other than above */
    OTHER { override fun toString() = "Other" },

    /** Written works not published in volumes */
    NOVEL { override fun toString() = "Novel" },

    /** Self-published works */
    DOUJINSHI { override fun toString() = "Doujinshi" },

    /** Japanese Anime */
    ANIME { override fun toString() = "Anime" },

    /** Constant for unknown enum values */
    UNKNOWN,
}

/**
 * The type of media ranking
 */
enum class MediaRankType {
    /** Ranking is based on the media's ratings/score */
    RATED,

    /** Ranking is based on the media's popularity */
    POPULAR,

    /** Constant for unknown enum values */
    UNKNOWN,
}

////////////////////////////////////////////////////////////////////////////////////////////////////

/** Just wrapper for `List<String>` to avoid adding too wide converter for it */
@Parcelize
data class MediaTitleSynonym constructor(
    val title: String = "",
) : Parcelable

/** Just wrapper for `List<String>` to avoid adding too wide converter for it */
@Parcelize
data class MediaGenre constructor(
    val genre: String = "",
) : Parcelable

/** A tag that describes a theme or element of the media */
@Parcelize
data class MediaTag constructor(
    /** The id of the tag */
    /*val id: Int = -1,*/

    /** The name of the tag */
    val name: String = "",

    /** A general description of the tag */
    val description: String = "",

    /** The categories of tags this tag belongs to */
    val category: String = "",

    /** The relevance ranking of the tag out of the 100 for this media */
    val rank: Int = -1,

    /** If the tag could be a spoiler for any media */
    val isGeneralSpoiler: Boolean = false,

    /** If the tag is a spoiler for this media */
    val isMediaSpoiler: Boolean = false,

    /** If the tag is only for adult 18+ media */
    val isAdult: Boolean = false,
) : Parcelable

/**
 * An external link to another site related to the media
 */
@Parcelize
data class MediaExternalLink constructor(
    /** The id of the external link */
    /*val id: Int = -1,*/

    /**
     * The URL of the external link
     *
     * TODO: use `URL` type instead of raw `String`
     */
    val url: String = "",

    /** The site location of the external link */
    val site: String = "",
) : Parcelable

/**
 * Data and links to legal streaming episodes on external sites
 */
@Parcelize
data class MediaStreamingEpisode constructor(
    /** Title of the episode */
    val title: String = "",

    /**
     * URL of episode image thumbnail
     *
     * TODO: use `URL` type instead of raw `String`
     */
    val thumbnail: String = "",

    /**
     * The URL of the episode
     *
     * TODO: use `URL` type instead of raw `String`
     */
    val url: String = "",

    /** The site location of the streaming episodes */
    val site: String = "",
) : Parcelable

/** The ranking of a media in a particular time span and format compared to other media */
@Parcelize
data class MediaRank constructor(
    /** The id of the rank */
    /*val id: Int = -1,*/

    /** The numerical rank of the media */
    val rank: Int = -1,

    /** The type of ranking */
    val type: MediaRankType = MediaRankType.UNKNOWN,

    /** The format the media is ranked within */
    val format: MediaFormat = MediaFormat.UNKNOWN,

    /**
     * The year the media is ranked within
     *
     * TODO: use `Year` type instead of raw `Int`
     */
    val year: Int = -1,

    /** The season the media is ranked within */
    val season: MediaSeason = MediaSeason.UNKNOWN,

    /** If the ranking is based on all time instead of a season/year */
    val allTime: Boolean = false,

    /** String that gives context to the ranking type and time span */
    val context: String = "",
) : Parcelable

@Parcelize
data class MediaStudio constructor(
    /** The unique identifier of the studio **/
    val id: Int = -1,

    /** The name of the studio */
    val name: String = "",

    /** Whether the studio is an animation studio or a different kind of company */
    val isAnimationStudio: Boolean = false,

    /** The media the studio has worked on */
    /*val medias: List<Media> = emptyList(),*/

    /** The URL for the studio page on the AniList website */
    // TODO: use URL class instead of raw `String`
    val siteUrl: String = "",

    /** If the studio is marked as favourite by the currently authenticated user */
    /*val isFavorite: Boolean = false,*/

    /** The amount of user's who have added the studio to favorites */
    val favorites: Int = -1,
) : Parcelable

////////////////////////////////////////////////////////////////////////////////////////////////////

/** An anime/manga detailed information from Anilist */
@Entity(tableName = "media_collection")
@Parcelize
data class Media constructor(
    /** The unique identifier ("id") of the media, also serve as a SQLite primary key */
    @PrimaryKey
    var anilistId: Long = -1,

    /** The MyAnimeList ("mal") id of the media */
    var malId: Long = -1,

    /** The URL for the media page on the AniList website */
    var anilistUrl: URL = INVALID_URL,

    /** The URL for the media page on the MyAnimeList("mal") website */
    var malUrl: URL = INVALID_URL,

    /**
     * When the media's data was last updated (seconds from the epoch of 1970-01-01T00:00:00Z)
     *
     * TODO: use in `RemoteMediator` implementation to minimize cache miss
     */
    var updatedAt: LocalDateTime? = null,

    /** The official titles of the media in various languages */

    /** The romanization of the native language title */
    var romajiTitle: String = "",

    /** The official English title */
    val englishTitle: String = "",

    /** Official title in it's native language (usually Japanese) */
    val nativeTitle: String = "",

    /** Alternative titles of the media */
    var titleSynonyms: List<MediaTitleSynonym> = emptyList(),

    /**
     * The type of the media: anime or manga
     *
     * NOTE: only anime type is currently supported
     */
    /*var type: MediaType = MediaType.UNKNOWN,*/

    /** The format the media was released in */
    var format: MediaFormat = MediaFormat.UNKNOWN,

    /** The current releasing status of the media */
    var status: MediaStatus = MediaStatus.UNKNOWN,

    /** Short description of the media's story and characters */
    var description: String = "",

    /** The first official release date of the media (day+month+year) */
    var startDate: LocalDate? = null,

    /** The last official release date of the media */
    var endDate: LocalDate? = null,

    /** The season the media was initially released in */
    var startSeason: MediaSeason = MediaSeason.UNKNOWN,

    /** The season year the media was initially released in */
    var startSeasonYear: Int = -1,

    /**
     * The amount of episodes the anime has when complete
     *
     * NOTE: can be absent, if anime is infinite one or just haven't the end date defined
     */
    var episodeCount: Int = -1,

    /** Manga only: the amount of chapters the manga has when complete */
    /*var chapterCount: Int = -1,*/

    /** Manga only: the amount of volumes the manga has when complete */
    /*var volumeCount: Int = -1,*/

    /** The general length of each anime episode in minutes */
    var episodeDuration: Int = -1,

    // The media's next episode airing schedule

    /**
     * The time the episode airs at
     *
     * TODO: use `ZonedScheduledTime` type instead of raw `Int`
     */
    var nextEpisodeAiringAt: ZonedScheduledTime? = null,

    /** Seconds until episode starts airing */
    val nextEpisodeTimeUntilAiring: LocalDateTime? = null,

    /** The airing episode number */
    var nextEpisodeNo: Int = -1,

    /** Where the media was created (ISO 3166-1 alpha-2, i.e. two-letter country code) */
    var countryOfOrigin: String = "",

    /** If the media is officially licensed or a self-published ("doujin") release */
    var isLicensed: Boolean = true,

    /** Source type the media was adapted from */
    var source: MediaSource = MediaSource.UNKNOWN,

    /** Official Twitter hashtags for the media */
    var hashtag: String = "",

    // Media trailer or advertisement

    /** The site the video is hosted by (currently either "youtube" or "dailymotion") */
    val trailerSite: String = "",

    /**
     * The URL for the thumbnail image of the video
     *
     * TODO: use `URL` type instead of raw `String`
     */
    val trailerThumbnail: String = "",

    // The cover images of the media

    /**
     * The cover image URL of the media at its largest size.
     * If this size isn't available, large will be provided instead
     *
     * TODO: use `URL` type instead of raw `String`
     */
    val coverImageExtraLarge: String = "",

    /**
     * The cover image URL of the media at a large size
     *
     * TODO: use `URL` type instead of raw `String`
     */
    var coverImageLarge: String = "",

    /**
     * The cover image URL of the media at medium size
     *
     * TODO: use `URL` type instead of raw `String`
     */
    val coverImageMedium: String = "",

    /**
     * Average #hex color of cover image
     *
     * TODO: use `Color` type instead of raw `String`
     */
    var coverImageColor: String = "",

    /** The banner image of the media */
    var bannerImage: String = "",

    /** The genres of the media */
    var genres: List<MediaGenre> = emptyList(),

    // The media appreciation by users statistics

    /** A weighted average score of all the user's scores of the media (in percents) */
    val averageScore: Int = -1,

    /** Mean score of all the user's scores of the media (in percents) */
    val meanScore: Int = -1,

    /** The number of users with the media on their list */
    var popularity: Int = -1,

    /** The amount of user's who have added the media to favorites */
    val favorites: Int = -1,

    /** The amount of related activity in the past hour */
    val trending: Int = -1,

    /** List of tags that describes elements and themes of the media */
    var tags: List<MediaTag> = emptyList(),

    /** Other media in the same or connecting franchise */
    /*var relations: List<Media> = mediaList(),*/

    /** The characters in the media */
    /*var characters: List<MediaCharacter> = emptyList(),*/

    /** The staff who produced the media */
    /*var staff: List<MediaAuthor> = emptyList(),*/

    /** The companies who produced the media */
    var studios: List<MediaStudio> = emptyList(),

    /** Whether the media is marked as favourite by the current authenticated user */
    /*var isFavorite: Boolean = false,*/

    /**
     * Whether the media is intended only for 18+ adult audiences
     *
     * NOTE: currently such kind of media is filtered on GraphQL query step, to avoid legal risks
     */
    /*var isAdult: Boolean = false,*/

    /** External links to another site related to the media */
    var externalLinks: List<MediaExternalLink> = emptyList(),

    /** Data and links to legal streaming episodes on external sites */
    var streamingEpisodes: List<MediaStreamingEpisode> = emptyList(),

    /** The ranking of the media in a particular time span and format compared to other media */
    var rankings: List<MediaRank> = emptyList(),

    // Calculated fields which not present in Anilist data

    /**
     * The season number of the media
     *
     * NOTE: Anilist response don't contain this field, so it calculated using "relations" info:
     * just recursively request prequels until they exists, and increase the counter
     */
    var season: Int = -1,

    /**
     * Minimal accepted age for watching: e.g. 16 years old only and older
     *
     * NOTE: Anilist response don't contain this field, so it calculated using "tags" info:
     * some tags describe target auditory, e.g. "shounen" means "males 10-17 years old"
     */
    var minAge: Int = -1,

) : Parcelable {

    // Required for static extension functions
    companion object
}

/** Local favorite media data */
@Entity(tableName = "favorite_media_collection")
@Parcelize
data class FavoriteMedia constructor(
    /** The unique identifier ("id") of the media, also serve as a SQLite primary key */
    @PrimaryKey
    val anilistId: Long = -1,
) : Parcelable
