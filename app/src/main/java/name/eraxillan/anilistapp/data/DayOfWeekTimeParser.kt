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

import timber.log.Timber
import java.lang.NumberFormatException
import java.time.*
import java.time.format.DateTimeParseException
import java.time.zone.ZoneRulesException
import java.util.*


class DayOfWeekTimeParser {
    companion object {
        data class DowTime(
            val dow: DayOfWeek,
            val lt: LocalTime,
            val tzOffset: ZoneOffset,
            val tzName: ZoneId
        )

        /**
         * "W-1T20:30:00.000000+03:00[Europe/Moscow]"
         */
        fun parse(input: CharSequence): DowTime? {
            // Time zone can contain 'w' and 't' chars, so first of all extract it
            val baseTokens = input.split("[", "]").filter { it.isNotEmpty() }
            if (baseTokens.size != 2) {
                Timber.e("Invalid scheduled time format!")
                Timber.e("Tokens (${baseTokens.size}):\n ${baseTokens.joinToString("\n")}")
                return null
            }

            val sctString = baseTokens[0]
            val sctTokens = sctString
                .uppercase(Locale.US)
                .split("W-", "T")
                .filter { it.isNotEmpty() }
            if (sctTokens.size != 2) {
                Timber.e("Invalid scheduled time format!")
                Timber.e("Tokens = \n${sctTokens.joinToString("\n")}")
                return null
            }

            val tzTokens = sctTokens[1].split(Regex("(?=[+-])"))
            if (tzTokens.size != 2) {
                Timber.e("Invalid scheduled time format!")
                Timber.e("Tokens = \n${tzTokens.joinToString("\n")}")
                return null
            }

            val dowStr = sctTokens[0]
            val ltStr = tzTokens[0]
            val tzOffsetStr = tzTokens[1]
            val tzNameStr = baseTokens[1]

            /*if (BuildConfig.DEBUG) {
                Timber.d("Raw day of week: $dowStr")
                Timber.d("Raw local time: $ltStr")
                Timber.d("Raw tz offset: $tzOffsetStr")
                Timber.d("Raw tz name: $tzNameStr")
            }*/

            val dowInt = try {
                dowStr.toInt()
            } catch (exc: NumberFormatException) {
                Timber.e("Invalid day of week string '$dowStr'!")
                return null
            }

            val dow = try {
                DayOfWeek.of(dowInt)
            } catch (exc: DateTimeException) {
                Timber.e("Invalid day of week integer '$dowInt'!")
                return null
            }

            val lt = try {
                LocalTime.parse(ltStr)
            } catch (exc: DateTimeParseException) {
                Timber.e("Invalid local time string '$ltStr'!")
                return null
            }

            val tzOffset = try {
                ZoneOffset.of(tzOffsetStr)
            } catch (exc: DateTimeException) {
                Timber.e("Invalid time zone offset string '$tzOffsetStr'!")
                return null
            } catch (exc: ZoneRulesException) {
                Timber.e("Time zone configuration problem found!")
                return null
            }

            val tzName = try {
                ZoneId.of(tzNameStr)
            } catch (exc: DateTimeException) {
                Timber.e("Invalid time zone offset string '$tzOffsetStr'!")
                return null
            } catch (exc: ZoneRulesException) {
                Timber.e("Time zone configuration problem found!")
                return null
            }

            return DowTime(dow, lt, tzOffset, tzName)
        }
    }
}
