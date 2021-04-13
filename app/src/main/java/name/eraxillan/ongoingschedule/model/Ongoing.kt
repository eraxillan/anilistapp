package name.eraxillan.ongoingschedule.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
//import java.time.Year
//import java.net.URL

enum class OngoingType { TV, OVA, ONA }

/*
 https://en.wikipedia.org/wiki/Motion_Picture_Association_film_rating_system
 https://www.kinopoisk.ru/mpaa/

enum class MpaaRating {
    G,     // General audiences: all ages admitted
    PG,    // Parental guidance suggested: some material may not be suitable for children
    PG_13, // Parents strongly cautioned: some material may be inappropriate for children under 13
    R,     // Restricted: under 17 requires accompanying parent or adult guardian
    NC_17  // Adults only: no one 17 and under admitted
}
 */

// FIXME: add Kotlin documentation comments
/**
 *
 */
@Entity
data class Ongoing constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,             // Database primary key
    var url: String = "",             // Wakanim/Crunchyroll URL (TODO: `java.net.URL`)
    var season: Int = -1,             // Season number
    var originalName: String = "",    // Original Japanese name in Romaji
    var type: Int = -1,               // Type (TODO: `OngoingType`)
    var year: Int = -1,               // Launch year (TODO: `java.time.Year`?)
    var latestEpisode: Int = -1,      // Latest episode number: e.g. 5, i.e. 5th from 12
    var totalEpisodes: Int = -1,      // Total episode count: e.g. 12
    var nextEpisodeDate: Long = -1,   // Latest episode launch date as timestamp (TODO: `java.util.Date`)
    // FIXME: cause Room compile error due to unsupported `List<String>` type, use TypeConverters
//    var audioTracks: List<String> = emptyList(),    // "en", "ru"
//    var audioSubtitles: List<String> = emptyList(), // "en", "ru"
    var minAge: Int = -1,             // Minimal accepted age: e.g. 16 or 18
) : Parcelable {

    constructor(parcel: Parcel) : this(
        /*Long? */ parcel.readValue(Long::class.java.classLoader) as? Long,
        /*String*/ parcel.readString() ?: "",
        /*Int   */ parcel.readInt(),
        /*String*/ parcel.readString() ?: "",
        /*Int   */ parcel.readInt(),
        /*Int   */ parcel.readInt(),
        /*Int   */ parcel.readInt(),
        /*Int   */ parcel.readInt(),
        /*Long  */ parcel.readLong(),
        /*Int   */ parcel.readInt()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(destination: Parcel, flags: Int) {
        /*Long? */ destination.writeValue(id)
        /*String*/ destination.writeString(url)
        /*Int   */ destination.writeInt(season)
        /*String*/ destination.writeString(originalName)
        /*Int   */ destination.writeInt(type)
        /*Int   */ destination.writeInt(year)
        /*Int   */ destination.writeInt(latestEpisode)
        /*Int   */ destination.writeInt(totalEpisodes)
        /*Long  */ destination.writeLong(nextEpisodeDate)
        /*Int   */ destination.writeInt(minAge)
    }

    companion object CREATOR : Parcelable.Creator<Ongoing> {
        override fun createFromParcel(parcel: Parcel): Ongoing {
            return Ongoing(parcel)
        }

        override fun newArray(size: Int): Array<Ongoing?> {
            return arrayOfNulls(size)
        }
    }
}
