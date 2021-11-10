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
import androidx.room.*
import kotlinx.parcelize.Parcelize
import name.eraxillan.anilistapp.db.ZonedScheduledTime
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime

// FIXME: implement custom App class and replace this with string resource
val INVALID_URL = URL("https://www.invalid.com")

////////////////////////////////////////////////////////////////////////////////////////////////////
// FIXME: rename to RemoteMedia

/** An anime/manga detailed information from Anilist */
@Entity(tableName = "media_collection")
@Parcelize
data class Media constructor(
    /** The unique identifier ("id") of the media, also serve as a SQLite primary key */
    @PrimaryKey
    @ColumnInfo(name = "anilist_id")
    var anilistId: Long = -1,

    /** The MyAnimeList ("mal") id of the media */
    @ColumnInfo(name = "mal_id")
    var malId: Long = -1,

    /** The URL for the media page on the AniList website */
    @ColumnInfo(name = "anilist_url")
    var anilistUrl: URL = INVALID_URL,

    /** The URL for the media page on the MyAnimeList("mal") website */
    @ColumnInfo(name = "mal_url")
    var malUrl: URL = INVALID_URL,

    /**
     * When the media's data was last updated (seconds from the epoch of 1970-01-01T00:00:00Z)
     *
     * TODO: use in `RemoteMediator` implementation to minimize cache miss
     */
    @ColumnInfo(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    /** The official titles of the media in various languages */

    /** The romanization of the native language title */
    @ColumnInfo(name = "romaji_title")
    var romajiTitle: String = "",

    /** The official English title */
    @ColumnInfo(name = "english_title")
    var englishTitle: String = "",

    /** Official title in it's native language (usually Japanese) */
    @ColumnInfo(name = "native_title")
    var nativeTitle: String = "",

    /** Alternative titles of the media */
    @Ignore
    var titleSynonyms: List<MediaTitleSynonym> = emptyList(),

    /**
     * The type of the media: anime or manga
     *
     * NOTE: only anime type is currently supported
     */
    /*@ColumnInfo(name = "type")*/
    /*var type: MediaType = MediaType.UNKNOWN,*/

    /** The format the media was released in */
    @ColumnInfo(name = "format")
    var format: MediaFormatEnum = MediaFormatEnum.UNKNOWN,

    /** The current releasing status of the media */
    @ColumnInfo(name = "status")
    var status: MediaStatus = MediaStatus.UNKNOWN,

    /** Short description of the media's story and characters */
    @ColumnInfo(name = "description")
    var description: String = "",

    /** The first official release date of the media (day+month+year) */
    @ColumnInfo(name = "start_date")
    var startDate: LocalDate? = null,

    /** The last official release date of the media */
    @ColumnInfo(name = "end_date")
    var endDate: LocalDate? = null,

    /** The season the media was initially released in */
    @ColumnInfo(name = "start_season")
    var startSeason: MediaSeason = MediaSeason.UNKNOWN,

    /** The season year the media was initially released in */
    @ColumnInfo(name = "start_season_year")
    var startSeasonYear: Int = -1,

    /**
     * The amount of episodes the anime has when complete
     *
     * NOTE: can be absent, if anime is infinite one or just haven't the end date defined
     */
    @ColumnInfo(name = "episode_count")
    var episodeCount: Int = -1,

    /** Manga only: the amount of chapters the manga has when complete */
    /*@ColumnInfo(name = "chapter_count")*/
    /*var chapterCount: Int = -1,*/

    /** Manga only: the amount of volumes the manga has when complete */
    /*@ColumnInfo(name = "volume_count")*/
    /*var volumeCount: Int = -1,*/

    /** The general length of each anime episode in minutes */
    @ColumnInfo(name = "episode_duration")
    var episodeDuration: Int = -1,

    // The media's next episode airing schedule

    /**
     * The time the episode airs at
     *
     * TODO: use `ZonedScheduledTime` type instead of raw `Int`
     */
    @ColumnInfo(name = "next_episode_airing_at")
    var nextEpisodeAiringAt: ZonedScheduledTime? = null,

    /** Seconds until episode starts airing */
    @ColumnInfo(name = "next_episode_time_until_airing")
    var nextEpisodeTimeUntilAiring: LocalDateTime? = null,

    /** The airing episode number */
    @ColumnInfo(name = "next_episode_number")
    var nextEpisodeNo: Int = -1,

    /** Where the media was created (ISO 3166-1 alpha-2, i.e. two-letter country code) */
    @ColumnInfo(name = "country_of_origin")
    var countryOfOrigin: String = "",

    /** If the media is officially licensed or a self-published ("doujin") release */
    @ColumnInfo(name = "is_licensed")
    var isLicensed: Boolean = true,

    /** Source type the media was adapted from */
    @ColumnInfo(name = "source")
    var source: MediaSourceEnum = MediaSourceEnum.UNKNOWN,

    /** Official Twitter hashtags for the media */
    @ColumnInfo(name = "hashtag")
    var hashtag: String = "",

    // Media trailer or advertisement

    /** The site the video is hosted by (currently either "youtube" or "dailymotion") */
    @ColumnInfo(name = "trailer_site")
    var trailerSite: String = "",

    /**
     * The URL for the thumbnail image of the video
     *
     * TODO: use `URL` type instead of raw `String`
     */
    @ColumnInfo(name = "trailer_thumbnail")
    var trailerThumbnail: String = "",

    // The cover images of the media

    /**
     * The cover image URL of the media at its largest size.
     * If this size isn't available, large will be provided instead
     *
     * TODO: use `URL` type instead of raw `String`
     */
    @ColumnInfo(name = "cover_image_extra_large")
    var coverImageExtraLarge: String = "",

    /**
     * The cover image URL of the media at a large size
     *
     * TODO: use `URL` type instead of raw `String`
     */
    @ColumnInfo(name = "cover_image_large")
    var coverImageLarge: String = "",

    /**
     * The cover image URL of the media at medium size
     *
     * TODO: use `URL` type instead of raw `String`
     */
    @ColumnInfo(name = "cover_image_medium")
    var coverImageMedium: String = "",

    /**
     * Average #hex color of cover image
     *
     * TODO: use `Color` type instead of raw `String`
     */
    @ColumnInfo(name = "cover_image_color")
    var coverImageColor: String = "",

    /** The banner image of the media */
    @ColumnInfo(name = "banner_image")
    var bannerImage: String = "",

    /** The genres of the media */
    @Ignore
    var genres: List<MediaGenre> = emptyList(),

    // The media appreciation by users statistics

    /** A weighted average score of all the user's scores of the media (in percents) */
    @ColumnInfo(name = "average_score")
    var averageScore: Int = -1,

    /** Mean score of all the user's scores of the media (in percents) */
    @ColumnInfo(name = "mean_score")
    var meanScore: Int = -1,

    /** The number of users with the media on their list */
    @ColumnInfo(name = "popularity")
    var popularity: Int = -1,

    /** The amount of user's who have added the media to favorites */
    @ColumnInfo(name = "favorites")
    var favorites: Int = -1,

    /** The amount of related activity in the past hour */
    @ColumnInfo(name = "trending")
    var trending: Int = -1,

    /** List of tags that describes elements and themes of the media */
    @Ignore
    var tags: List<MediaTag> = emptyList(),

    /** Other media in the same or connecting franchise */
    /*@Ignore*/
    /*var relations: List<Media> = mediaList(),*/

    /** The characters in the media */
    /*@Ignore*/
    /*var characters: List<MediaCharacter> = emptyList(),*/

    /** The staff who produced the media */
    /*@Ignore*/
    /*var staff: List<MediaAuthor> = emptyList(),*/

    /** The companies who produced the media */
    @Ignore
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
    @Ignore
    var externalLinks: List<MediaExternalLink> = emptyList(),

    /** Data and links to legal streaming episodes on external sites */
    @Ignore
    var streamingEpisodes: List<MediaStreamingEpisode> = emptyList(),

    /** The ranking of the media in a particular time span and format compared to other media */
    @Ignore
    var rankings: List<MediaRank> = emptyList(),

    // Calculated fields which not present in Anilist data

    /**
     * The season number of the media
     *
     * NOTE: Anilist response don't contain this field, so it calculated using "relations" info:
     * just recursively request prequels until they exists, and increase the counter
     */
    @ColumnInfo(name = "season")
    var season: Int = -1,

    /**
     * Minimal accepted age for watching: e.g. 16 years old only and older
     *
     * NOTE: Anilist response don't contain this field, so it calculated using "tags" info:
     * some tags describe target auditory, e.g. "shounen" means "males 10-17 years old"
     */
    @ColumnInfo(name = "min_age")
    var minAge: Int = -1,

    ) : Parcelable {

    // Required for static extension functions
    companion object
}
