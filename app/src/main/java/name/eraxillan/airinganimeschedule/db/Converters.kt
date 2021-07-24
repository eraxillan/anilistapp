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

import androidx.room.TypeConverter
import name.eraxillan.airinganimeschedule.model.AnimeTitle
import java.net.URL
import java.time.LocalDate

object DatabaseTypeConverters {
    /*
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            formatter.parse(value, OffsetDateTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(formatter)
    }
    */

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
    fun toAnimeTitle(value: String?): AnimeTitle? {
        return value?.let {
            val list = it.split(listDelimiter)
            check(list.size == 3)
            AnimeTitle(romaji = list[0], english = list[1], native = list[2])
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromAnimeTitle(title: AnimeTitle?): String? {
        return title?.let {
            "${it.romaji}${listDelimiter}${it.english}${listDelimiter}${it.native}"
        }
    }

    private const val listDelimiter = ":::::"
}
