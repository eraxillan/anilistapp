package name.eraxillan.ongoingschedule.db

import android.util.Log
import name.eraxillan.ongoingschedule.BuildConfig
import java.lang.NumberFormatException
import java.time.*
import java.time.format.DateTimeParseException
import java.time.zone.ZoneRulesException
import java.util.*


class DowTimeParser {
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
        fun parse(input: String): DowTime? {
            // Time zone can contain 'w' and 't' chars, so first of all extract it
            val baseTokens = input.split("[", "]").filter { it.isNotEmpty() }
            if (baseTokens.size != 2) {
                Log.e(TAG, "Invalid scheduled time format!")
                Log.e(TAG, "Tokens (${baseTokens.size}):\n ${baseTokens.joinToString("\n")}")
                return null
            }

            val sctString = baseTokens[0]
            val sctTokens = sctString
                .uppercase(Locale.US)
                .split("W-", "T")
                .filter { it.isNotEmpty() }
            if (sctTokens.size != 2) {
                Log.e(TAG, "Invalid scheduled time format!")
                Log.e(TAG, "Tokens = \n" + sctTokens.joinToString("\n"))
                return null
            }

            val tzTokens = sctTokens[1].split(Regex("(?=[+-])"))
            if (tzTokens.size != 2) {
                Log.e(TAG, "Invalid scheduled time format!")
                Log.e(TAG, "Tokens = \n" + tzTokens.joinToString("\n"))
                return null
            }

            val dowStr = sctTokens[0]
            val ltStr = tzTokens[0]
            val tzOffsetStr = tzTokens[1]
            val tzNameStr = baseTokens[1]

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Raw day of week: $dowStr")
                Log.d(TAG, "Raw local time: $ltStr")
                Log.d(TAG, "Raw tz offset: $tzOffsetStr")
                Log.d(TAG, "Raw tz name: $tzNameStr")
            }

            val dowInt = try {
                dowStr.toInt()
            } catch (exc: NumberFormatException) {
                Log.e(TAG, "Invalid day of week string '$dowStr'!")
                return null
            }

            val dow = try {
                DayOfWeek.of(dowInt)
            } catch (exc: DateTimeException) {
                Log.e(TAG, "Invalid day of week integer '$dowInt'!")
                return null
            }

            val lt = try {
                LocalTime.parse(ltStr)
            } catch (exc: DateTimeParseException) {
                Log.e(TAG, "Invalid local time string '$ltStr'!")
                return null
            }

            val tzOffset = try {
                ZoneOffset.of(tzOffsetStr)
            } catch (exc: DateTimeException) {
                Log.e(TAG, "Invalid time zone offset string '$tzOffsetStr'!")
                return null
            } catch (exc: ZoneRulesException) {
                Log.e(TAG, "Time zone configuration problem found!")
                return null
            }

            val tzName = try {
                ZoneId.of(tzNameStr)
            } catch (exc: DateTimeException) {
                Log.e(TAG, "Invalid time zone offset string '$tzOffsetStr'!")
                return null
            } catch (exc: ZoneRulesException) {
                Log.e(TAG, "Time zone configuration problem found!")
                return null
            }

            return DowTime(dow, lt, tzOffset, tzName)
        }

        private val TAG = DowTimeParser::class.java.simpleName
    }
}
