package name.eraxillan.ongoingschedule.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import name.eraxillan.ongoingschedule.db.ZonedScheduledTime
import java.net.URL
import java.time.LocalDate


// Just for brevity in this module
private typealias DMY = LocalDate
private typealias ZST = ZonedScheduledTime

// FIXME: implement custom App class and replace with string resource
private val INVALID_URL = URL("https://www.invalid.com")


/**
 * An ongoing anime description
 */
@Entity(tableName = "ongoings")
data class Ongoing constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,             // Database primary key
    var url: URL = INVALID_URL,       // MyAnimeList/Wakanim/Crunchyroll/etc. URL
    var season: Int = -1,             // Season number
    var originalName: String = "",    // Original Japanese name in Romaji
    var latestEpisode: Int = -1,      // Latest episode number: e.g. 5, i.e. 5th from 12
    var totalEpisodes: Int = -1,      // Total episode count: e.g. 12
    var releaseDate: DMY? = null,     // Aired day+month+year
    var nextEpisodeDate: ZST? = null, // Latest episode launch day+time with timezone
    var minAge: Int = -1,             // Minimal accepted age: e.g. 16 or 18
) : Parcelable {

    constructor(parcel: Parcel) : this(
        /*Long? */ parcel.readValue(Long::class.java.classLoader) as? Long,
        /*URL   */ parcel.readValue(URL::class.java.classLoader) as URL,
        /*Int   */ parcel.readInt(),
        /*String*/ parcel.readString() ?: "",
        /*Int   */ parcel.readInt(),
        /*Int   */ parcel.readInt(),
        /*DMY   */ parcel.readValue(DMY::class.java.classLoader) as? DMY,
        /*ZST   */ parcel.readValue(ZST::class.java.classLoader) as? ZST,
        /*Int   */ parcel.readInt()

        // parcel.readSerializable() as? OffsetDateTime,
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(destination: Parcel, flags: Int) {
        /*Long? */ destination.writeValue(id)
        /*URL   */ destination.writeValue(url)
        /*Int   */ destination.writeInt(season)
        /*String*/ destination.writeString(originalName)
        /*Int   */ destination.writeInt(latestEpisode)
        /*Int   */ destination.writeInt(totalEpisodes)
        /*DMY   */ destination.writeValue(releaseDate)
        /*ZST   */ destination.writeValue(nextEpisodeDate)
        /*Int   */ destination.writeInt(minAge)

        // destination.writeSerializable(nextEpisodeDate)
    }

    companion object CREATOR : Parcelable.Creator<Ongoing> {
        override fun createFromParcel(parcel: Parcel): Ongoing {
            return Ongoing(parcel)
        }

        override fun newArray(size: Int): Array<Ongoing?> {
            return arrayOfNulls(size)
        }
    }

    fun copyDataFields(other: Ongoing) {
        // Ignore `id` and url `fields`, other filled by parser
        season = other.season
        originalName = other.originalName
        latestEpisode = other.latestEpisode
        totalEpisodes = other.totalEpisodes
        releaseDate = other.releaseDate
        nextEpisodeDate = other.nextEpisodeDate
        minAge = other.minAge
    }
}
