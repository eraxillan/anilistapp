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

package name.eraxillan.anilistapp.data

import androidx.room.TypeConverter
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
}
