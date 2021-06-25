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

package name.eraxillan.airinganimeschedule.db

import android.util.Log
import name.eraxillan.airinganimeschedule.AiringAnimeQuery
import name.eraxillan.airinganimeschedule.model.AiringAnime
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun mediumToAiringAnime(input: AiringAnimeQuery.Medium): AiringAnime {

    fun hasAdultTags(): Boolean {
        return input.tags?.any { tag ->
            if (tag?.isAdult == true) {
                Log.e("name.eraxillan.animeapp", "Adult tag detected: '${tag.name}'")
                tag.isAdult
            } else false
        } == true
    }

    fun getMinAge(): Int {
        // NOTE: We already filtered strict 18+ titles in GraphQL query

        // All possible demographic-related tags and corresponding minimal age
        val demographicTagAges = mapOf(
            "kids" to 0,      // Males/Females 0-10
            "shounen" to 10,  // Shounen: Males 10-17
            "shoujo" to 10,   // Shoujo: Females 10-17
            "seinen" to 17,   // Seinen: Males 17+
            "josei" to 17     // Josei: Females 17+
        )

        // First of all, convert all tag names to lowercase to simplify string comparison
        val inputTags = input.tags?.map { it?.name?.lowercase() ?: "" } ?: emptyList()

        //
        Log.e("name.eraxillan.animeapp", "Anime tags: '$inputTags'")
        //

        // Now found the intersection between predefined demographic tags and input ones
        val intersectionTags = inputTags.intersect(demographicTagAges.keys)

        //
        Log.e("name.eraxillan.animeapp", "Anime demographic tags: '$intersectionTags'")
        //

        // Find maximum age available in demographic tags
        val minAgeTag = intersectionTags.maxByOrNull { demographicTagAges[it] ?: 0 }
        var minAge = demographicTagAges[minAgeTag] ?: -1

        // Finally search for adult tags and increase minimal age to 18 if at least one found
        if (hasAdultTags()) {
            minAge = 18
        }

        Log.e("name.eraxillan.animeapp", "Anime min age: $minAge")
        return minAge
    }

    // FIXME: detect user current time zone id/offset and remove this hardcode
    val zoneOffset = ZoneOffset.of("+3")
    val zoneId = ZoneId.of("Europe/Moscow")

    val airingTime = LocalDateTime.ofEpochSecond(
        input.nextAiringEpisode?.airingAt?.toLong() ?: 0L,
        0, zoneOffset
    )

    //
    Log.e("name.eraxillan.animeapp", "-------------------------------------------------")
    Log.e("name.eraxillan.animeapp", "Processing anime with title '${input.title?.romaji}'")
    //

    return AiringAnime(
        id = input.id.toLong(),
        url = URL(input.siteUrl),
        season = -1,//getSeason(),
        originalName = input.title?.romaji ?: "<romaji name not specified>",
        latestEpisode = (input.nextAiringEpisode?.episode?.minus(1)) ?: -1,
        totalEpisodes = input.episodes ?: -1,
        releaseDate = LocalDate.of(
            input.startDate?.year ?: 1970,
            input.startDate?.month ?: 1,
            input.startDate?.day ?: 1
        ),
        nextEpisodeDate = ZonedScheduledTime.of(
            airingTime.dayOfWeek,
            airingTime.toLocalTime(),
            zoneOffset,
            zoneId
        ),
        minAge = getMinAge()
    )
}
