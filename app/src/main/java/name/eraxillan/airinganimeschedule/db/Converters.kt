package name.eraxillan.airinganimeschedule.db

import androidx.room.TypeConverter
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DatabaseTypeConverters {
    /*
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return formatter.parse(value, OffsetDateTime::from)
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
            return URL(value)
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
            return LocalDate.parse(value)
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
            return ZonedScheduledTime.parse(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromScheduleTime(zst: ZonedScheduledTime?): String? {
        return zst?.toString()
    }
}
