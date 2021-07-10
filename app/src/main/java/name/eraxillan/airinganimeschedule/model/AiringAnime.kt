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

package name.eraxillan.airinganimeschedule.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import name.eraxillan.airinganimeschedule.db.ZonedScheduledTime
import java.net.URL
import java.time.LocalDate


// Shortcuts just for brevity in this module
private typealias DMY = LocalDate
private typealias ZST = ZonedScheduledTime
private typealias AAF = AiringAnimeFormat

// FIXME: implement custom App class and replace this with string resource
private val INVALID_URL = URL("https://www.invalid.com")

/**
 * The format the media was released in
 */
enum class AiringAnimeFormat {
    /**
     * Anime broadcast on television
     */
    TV,

    /**
     * Anime which are under 15 minutes in length and broadcast on television
     */
    TV_SHORT,

    /**
     * Anime movies with a theatrical release
     */
    MOVIE,

    /**
     * Special episodes that have been included in DVD/Blu-ray releases, picture dramas, pilots, etc
     */
    SPECIAL,

    /**
     * (Original Video Animation) Anime that have been released directly on DVD/Blu-ray without
     * originally going through a theatrical release or television broadcast
     */
    OVA,

    /**
     * (Original Net Animation) Anime that have been originally released online or are only available
     * through streaming services.
     */
    ONA,

    /**
     * Short anime released as a music video
     */
    MUSIC,

    /**
     * Professionally published manga with more than one chapter
     */
    MANGA,

    /**
     * Written books released as a series of light novels
     */
    NOVEL,

    /**
     * Manga with just one chapter
     */
    ONE_SHOT,

    /**
     * Auto generated constant for unknown enum values
     */
    UNKNOWN
}


/**
 * An anime description
 */
@Entity(tableName = "airing_animes")
@Parcelize
data class AiringAnime constructor(
    @PrimaryKey
    var anilistId: Int = -1,          // Anilist identifier
    var url: URL = INVALID_URL,       // Anilist/MyAnimeList/Wakanim/Crunchyroll/etc. URL
    var season: Int = -1,             // Season number
    var format: AAF = AAF.TV,         // The format the media was released in
    var originalName: String = "",    // Original Japanese name in Romaji
    var latestEpisode: Int = -1,      // Latest episode number: e.g. 5, i.e. 5th from 12
    var totalEpisodes: Int = -1,      // Total episode count: e.g. 12
    var releaseDate: DMY? = null,     // Aired day+month+year
    var nextEpisodeDate: ZST? = null, // Latest episode launch day+time with timezone
    var minAge: Int = -1,             // Minimal accepted age: e.g. 16 or 18
) : Parcelable {

    // Required for static extension functions
    companion object
}

@Entity(tableName = "favorite_animes")
@Parcelize
data class FavoriteAnime constructor(
    @PrimaryKey
    val anilistId: Int = -1,          // Anilist identifier
) : Parcelable
