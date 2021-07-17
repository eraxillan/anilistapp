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

package name.eraxillan.airinganimeschedule.utilities

import android.util.Log
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import name.eraxillan.airinganimeschedule.db.ZonedScheduledTime
import name.eraxillan.airinganimeschedule.model.AiringAnime
import java.io.InputStreamReader
import java.lang.ClassCastException
import java.lang.IllegalStateException
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeParseException

private const val LOG_TAG = "54BE6C87_JSD" // JSD = JsonSerializerDeserializer

private const val AA_ID = "id"
private const val AA_URL = "url"
private const val AA_SEASON = "season"
private const val AA_ORIGINAL_NAME = "originalName"
private const val AA_LATEST_EPISODE = "latestEpisode"
private const val AA_TOTAL_EPISODES = "totalEpisodes"
private const val AA_RELEASE_DATE = "releaseDate"
private const val AA_NEXT_EPISODE_DATE = "nextEpisodeDate"
private const val AA_MIN_AGE = "minAge"

private val gson: Gson by lazy {
    GsonBuilder()
        .registerTypeAdapter(AiringAnime::class.java,
            JsonSerializer<AiringAnime> { src, _, _ ->
                val jsonObject = JsonObject()
                // NOTE: we can use `::id.name` here, but it is not available in `fromJson` (no `this`)
                jsonObject.addProperty(AA_ID, src.anilistId)
                jsonObject.addProperty(AA_URL, src.url.toString())
                jsonObject.addProperty(AA_SEASON, src.season)
                jsonObject.addProperty(AA_ORIGINAL_NAME, src.originalName)
                jsonObject.addProperty(AA_LATEST_EPISODE, src.latestEpisode)
                jsonObject.addProperty(AA_TOTAL_EPISODES, src.totalEpisodes)
                jsonObject.addProperty(AA_RELEASE_DATE, src.releaseDate.toString())
                jsonObject.addProperty(AA_NEXT_EPISODE_DATE, src.nextEpisodeDate.toString())
                jsonObject.addProperty(AA_MIN_AGE, src.minAge)
                jsonObject
            })
        .registerTypeAdapter(AiringAnime::class.java,
            JsonDeserializer { dst, _, _ ->
                if (!dst.isJsonObject) {
                    Log.e(LOG_TAG, "Specified JSON text is not an object!")
                    return@JsonDeserializer null
                }

                val jsonObject = dst.asJsonObject
                // Check whether json have all required fields
                val keys = listOf(
                    AA_ID, AA_URL, AA_SEASON, AA_ORIGINAL_NAME, AA_LATEST_EPISODE,
                    AA_TOTAL_EPISODES, AA_RELEASE_DATE, AA_NEXT_EPISODE_DATE, AA_MIN_AGE
                )
                keys.forEach {
                    if (!jsonObject.has(it)) {
                        Log.e(LOG_TAG, "Required key `$it` is absent in specified JSON object!")
                        return@JsonDeserializer null
                    }
                }

                val anime = AiringAnime()
                var releaseDateString: String
                var nextEpisodeDateString: String
                with(anime) {
                    try {
                        anilistId = jsonObject.get("id").asLong
                        url = URL(jsonObject.get("url").asString)
                        season = jsonObject.get("season").asInt
                        originalName = jsonObject.get("originalName").asString
                        latestEpisode = jsonObject.get("latestEpisode").asInt
                        totalEpisodes = jsonObject.get("totalEpisodes").asInt
                        releaseDateString = jsonObject.get("releaseDate").asString
                        nextEpisodeDateString = jsonObject.get("nextEpisodeDate").asString
                        minAge = jsonObject.get("minAge").asInt
                    } catch (exc: ClassCastException) {
                        Log.e(LOG_TAG, "Invalid JSON value type: `${exc.message}`")
                        return@JsonDeserializer null
                    } catch (exc: IllegalStateException) {
                        Log.e(LOG_TAG, "Found JSON array instead of primitive: `${exc.message}`")
                        return@JsonDeserializer null
                    }

                    try {
                        releaseDate = LocalDate.parse(releaseDateString)
                    } catch (exc: DateTimeParseException) {
                        Log.e(LOG_TAG, "Unable to parse release date string: `$releaseDateString`")
                    }

                    nextEpisodeDate = ZonedScheduledTime.parse(nextEpisodeDateString)
                    if (nextEpisodeDate == null) {
                        Log.e(
                            LOG_TAG,
                            "Unable to parse next episode date string: `$nextEpisodeDateString`"
                        )
                        return@JsonDeserializer null
                    }
                }
                anime
            }
        ).create()
}

fun airingAnimeListFromJson(inputStreamReader: InputStreamReader): List<AiringAnime> {
    JsonReader(inputStreamReader).use { jsonReader ->
        val animeType = object : TypeToken<List<AiringAnime>>() {}.type
        return gson.fromJson(jsonReader, animeType)
    }
}
