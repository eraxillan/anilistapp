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

package name.eraxillan.anilistapp.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import name.eraxillan.anilistapp.model.*
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime

object DatabaseTypeConverters {

    @TypeConverter
    @JvmStatic
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            LocalDateTime.parse(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromLocalDateTime(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toURL(value: String?): URL? {
        return value?.let {
            URL(value)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromURL(ld: URL?): String? {
        return ld?.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let {
            LocalDate.parse(value)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromLocalDate(ld: LocalDate?): String? {
        return ld?.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toZonedScheduledTime(value: String?): ZonedScheduledTime? {
        return value?.let {
            ZonedScheduledTime.parse(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromScheduleTime(zst: ZonedScheduledTime?): String? {
        return zst?.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toMediaTitleSynonymList(value: String?): List<MediaTitleSynonym>? {
        return value?.let {
            Gson().fromJson(it, Array<MediaTitleSynonym>::class.java).toList()
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromMediaTitleSynonymList(synonyms: List<MediaTitleSynonym>?): String? {
        return synonyms?.let {
            Gson().toJson(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun toMediaTagList(value: String?): List<MediaTag>? {
        return value?.let {
            Gson().fromJson(value, Array<MediaTag>::class.java).toList()
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromMediaTagList(tags: List<MediaTag>?): String? {
        return tags?.let {
            Gson().toJson(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun toMediaExternalLinkList(value: String?): List<MediaExternalLink>? {
        return value?.let {
            Gson().fromJson(value, Array<MediaExternalLink>::class.java).toList()
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromMediaExternalLinkList(externalLinks: List<MediaExternalLink>?): String? {
        return externalLinks?.let {
            Gson().toJson(externalLinks)
        }
    }

    @TypeConverter
    @JvmStatic
    fun toMediaStreamingEpisodeList(value: String?): List<MediaStreamingEpisode>? {
        return value?.let {
            Gson().fromJson(value, Array<MediaStreamingEpisode>::class.java).toList()
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromMediaStreamingEpisodeList(streamingEpisodes: List<MediaStreamingEpisode>?): String? {
        return streamingEpisodes?.let {
            Gson().toJson(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun toMediaRankList(value: String?): List<MediaRank>? {
        return value?.let {
            Gson().fromJson(value, Array<MediaRank>::class.java).toList()
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromMediaRankList(ranks: List<MediaRank>?): String? {
        return ranks?.let {
            Gson().toJson(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun toMediaGenreList(value: String?): List<MediaGenre>? {
        return value?.let {
            Gson().fromJson(it, Array<MediaGenre>::class.java).toList()
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromMediaGenreList(genres: List<MediaGenre>?): String? {
        return genres?.let {
            Gson().toJson(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun toMediaStudioList(value: String?): List<MediaStudio>? {
        return value?.let {
            Gson().fromJson(value, Array<MediaStudio>::class.java).toList()
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromMediaStudioList(studios: List<MediaStudio>?): String? {
        return studios?.let {
            Gson().toJson(it)
        }
    }

    private const val listDelimiter = ":::::"
}
