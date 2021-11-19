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

package name.eraxillan.anilistapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import name.eraxillan.anilistapp.data.room.LocalMedia

/** The ranking of a media in a particular time span and format compared to other media */
@Entity(
    tableName = "media_ranks",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = LocalMedia::class,
            parentColumns = ["anilist_id"],
            childColumns = ["media_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
            // TODO: need testing
            //deferred = true
        ),
    ],
    // TODO: need testing
    indices = [
        androidx.room.Index("media_id"),
    ],
)
@Parcelize
data class MediaRank constructor(
    /** The id of the rank */
    @PrimaryKey @ColumnInfo(name = "rank_id")
    val rankId: Long = -1,

    /** The numerical rank of the media */
    @ColumnInfo(name = "rank")
    val rankValue: Int = -1,

    /** The type of ranking */
    @ColumnInfo(name = "type")
    val type: MediaRankingType = MediaRankingType.UNKNOWN,

    /** The format the media is ranked within */
    @ColumnInfo(name = "format")
    val format: MediaFormatEnum = MediaFormatEnum.UNKNOWN,

    /**
     * The year the media is ranked within
     *
     * TODO: use `Year` type instead of raw `Int`
     */
    @ColumnInfo(name = "year")
    val year: Int = -1,

    /** The season the media is ranked within */
    @ColumnInfo(name = "season")
    val season: MediaSeason = MediaSeason.UNKNOWN,

    /** If the ranking is based on all time instead of a season/year */
    @ColumnInfo(name = "all_time")
    val allTime: Boolean = false,

    /** String that gives context to the ranking type and time span */
    @ColumnInfo(name = "context")
    val context: String = "",

    /** One-to-many relation foreign key: points to associated media */
    @ColumnInfo(name = "media_id", defaultValue = "-1")
    val mediaId: Long = -1,
) : Parcelable {

    constructor(other: MediaRank, newMediaId: Long) : this(
        rankId = other.rankId,
        rankValue = other.rankValue,
        type = other.type,
        format = other.format,
        year = other.year,
        season = other.season,
        allTime = other.allTime,
        context = other.context,

        mediaId = newMediaId,
    )
}
