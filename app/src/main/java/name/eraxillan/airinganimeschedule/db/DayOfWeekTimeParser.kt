package name.eraxillan.airinganimeschedule.db

import android.util.Log
import name.eraxillan.airinganimeschedule.BuildConfig
import java.lang.NumberFormatException
import java.time.*
import java.time.format.DateTimeParseException
import java.time.zone.ZoneRulesException
import java.util.*


class DayOfWeekTimeParser {
    companion object {
        private val LOG_TAG = DayOfWeekTimeParser::class.java.simpleName

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
                Log.e(LOG_TAG, "Invalid scheduled time format!")
                Log.e(LOG_TAG, "Tokens (${baseTokens.size}):\n ${baseTokens.joinToString("\n")}")
                return null
            }

            val sctString = baseTokens[0]
            val sctTokens = sctString
                .uppercase(Locale.US)
                .split("W-", "T")
                .filter { it.isNotEmpty() }
            if (sctTokens.size != 2) {
                Log.e(LOG_TAG, "Invalid scheduled time format!")
                Log.e(LOG_TAG, "Tokens = \n" + sctTokens.joinToString("\n"))
                return null
            }

            val tzTokens = sctTokens[1].split(Regex("(?=[+-])"))
            if (tzTokens.size != 2) {
                Log.e(LOG_TAG, "Invalid scheduled time format!")
                Log.e(LOG_TAG, "Tokens = \n" + tzTokens.joinToString("\n"))
                return null
            }

            val dowStr = sctTokens[0]
            val ltStr = tzTokens[0]
            val tzOffsetStr = tzTokens[1]
            val tzNameStr = baseTokens[1]

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "Raw day of week: $dowStr")
                Log.d(LOG_TAG, "Raw local time: $ltStr")
                Log.d(LOG_TAG, "Raw tz offset: $tzOffsetStr")
                Log.d(LOG_TAG, "Raw tz name: $tzNameStr")
            }

            val dowInt = try {
                dowStr.toInt()
            } catch (exc: NumberFormatException) {
                Log.e(LOG_TAG, "Invalid day of week string '$dowStr'!")
                return null
            }

            val dow = try {
                DayOfWeek.of(dowInt)
            } catch (exc: DateTimeException) {
                Log.e(LOG_TAG, "Invalid day of week integer '$dowInt'!")
                return null
            }

            val lt = try {
                LocalTime.parse(ltStr)
            } catch (exc: DateTimeParseException) {
                Log.e(LOG_TAG, "Invalid local time string '$ltStr'!")
                return null
            }

            val tzOffset = try {
                ZoneOffset.of(tzOffsetStr)
            } catch (exc: DateTimeException) {
                Log.e(LOG_TAG, "Invalid time zone offset string '$tzOffsetStr'!")
                return null
            } catch (exc: ZoneRulesException) {
                Log.e(LOG_TAG, "Time zone configuration problem found!")
                return null
            }

            val tzName = try {
                ZoneId.of(tzNameStr)
            } catch (exc: DateTimeException) {
                Log.e(LOG_TAG, "Invalid time zone offset string '$tzOffsetStr'!")
                return null
            } catch (exc: ZoneRulesException) {
                Log.e(LOG_TAG, "Time zone configuration problem found!")
                return null
            }

            return DowTime(dow, lt, tzOffset, tzName)
        }
    }
}
