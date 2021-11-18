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

import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.LocaleListCompat
import timber.log.Timber
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


/**
 * Original: "2007-12-03T10:15:30+01:00[Europe/Paris]"
 * Final: "Mondays, 20:30 (MSK)" become "W-1T20:30:00.000000+03:00[Europe/Moscow]"
 * @see https://medium.com/androiddevelopers/room-time-2b4cf9672b98
 */
class ZonedScheduledTime private constructor(
    private var dayOfWeek: DayOfWeek,
    private var localTime: LocalTime,
    private var offset: ZoneOffset,
    private var zone: ZoneId
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(DayOfWeek::class.java.classLoader) as DayOfWeek,
        parcel.readValue(LocalTime::class.java.classLoader) as LocalTime,
        parcel.readValue(ZoneOffset::class.java.classLoader) as ZoneOffset,
        parcel.readValue(ZoneId::class.java.classLoader) as ZoneId
    )

    /**
     * Gets the day-of-week field, which is an enum {@code DayOfWeek}.
     * <p>
     * This method returns the enum {@link DayOfWeek} for the day-of-week.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link DayOfWeek#getValue() int value}.
     * <p>
     * Additional information can be obtained from the {@code DayOfWeek}.
     * This includes textual names of the values.
     *
     * @return the day-of-week, not null
     */
    fun getDayOfWeek(): DayOfWeek = dayOfWeek

    /**
     * Gets the {@code LocalTime} part of this date-time.
     * <p>
     * This returns a {@code LocalTime} with the same hour, minute, second and
     * nanosecond as this date-time.
     *
     * @return the time part of this date-time, not null
     */
    fun getLocalTime() = localTime

    /**
     * Gets the zone offset, such as '+01:00'.
     * <p>
     * This is the offset of the local time from UTC/Greenwich.
     *
     * @return the zone offset, not null
     */
    fun getOffset(): ZoneOffset = offset

    /**
     * Gets the time-zone, such as 'Europe/Paris'.
     * <p>
     * This returns the zone ID. This identifies the time-zone {@link ZoneRules rules}
     * that determine when and how the offset from UTC/Greenwich changes.
     * <p>
     * The zone ID may be same as the {@linkplain #getOffset() offset}.
     * If this is true, then any future calculations, such as addition or subtraction,
     * have no complex edge cases due to time-zone rules.
     * See also {@link #withFixedOffsetZone()}.
     *
     * @return the time-zone, not null
     */
    fun getZone(): ZoneId = zone

    fun getDisplayString(): String {
        val dow = dayOfWeek.getDisplayName(TextStyle.FULL, getDefaultLocale())
        val lt = localTime.toString()
        val pattern = DateTimeFormatter.ofPattern("zzz", getDefaultLocale())
        val tz = ZonedDateTime.now(zone).format(pattern)

        return "$dow, $lt ($tz)"
    }

    // `Any` methods customization
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that: ZonedScheduledTime = other as ZonedScheduledTime
        return dayOfWeek == that.dayOfWeek && localTime == that.localTime && offset == that.offset && zone == that.zone
    }

    override fun hashCode(): Int {
        return Objects.hash(dayOfWeek, localTime, offset, zone)
    }

    // This text output invented here in this method is styled to follow the designs of ISO 8601,
    // but is most certainly *not* defined in the standard.
    //
    // "W-1T20:30:00.000000+03:00[Europe/Moscow]"
    override fun toString(): String {
        return "W-" + dayOfWeek.value + "T" + localTime.toString() + offset.toString() + "[" + zone.toString() + "]"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(dayOfWeek)
        parcel.writeValue(localTime)
        parcel.writeValue(offset)
        parcel.writeValue(zone)
    }

    override fun describeContents(): Int {
        return 0
    }

    private fun getDefaultLocale() = LocaleListCompat.getDefault()[0]

    companion object CREATOR : Parcelable.Creator<ZonedScheduledTime> {
        override fun createFromParcel(parcel: Parcel): ZonedScheduledTime {
            return ZonedScheduledTime(parcel)
        }

        override fun newArray(size: Int): Array<ZonedScheduledTime?> {
            return arrayOfNulls(size)
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        fun of(dayOfWeek: DayOfWeek, localTime: LocalTime, offset: ZoneOffset, zone: ZoneId): ZonedScheduledTime {
            return ZonedScheduledTime(dayOfWeek, localTime, offset, zone)
        }

        // This text output invented here in this method is styled to follow the designs of ISO 8601,
        // but is most certainly *not* defined in the standard
        fun parse(input: CharSequence): ZonedScheduledTime? {
            val dt = DayOfWeekTimeParser.parse(input)
            if (dt == null) {
                Timber.e("Invalid zoned scheduled format!")
                Timber.e("Input: '$input'")
                return null
            }

            return ZonedScheduledTime(dt.dow, dt.lt, dt.tzOffset, dt.tzName)
        }

        // We do the date-time math by picking a date arbitrarily to use as a `LocalDateTime`.
        // For convenience, we might as well pick a year that starts on a Monday.
        // https://en.wikipedia.org/wiki/Common_year_starting_on_Monday
        // Let us go with 2001-01-01.
        /* private val BASELINE: LocalDateTime = LocalDateTime.of(2001, 1, 1, 0, 0, 0, 0) */
    }
}
