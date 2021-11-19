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

/**
 * An external link to another site related to the media
 */
@Entity(
    tableName = "media_external_links",
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
data class MediaExternalLink constructor(
    /** The id of the external link */
    @PrimaryKey
    @ColumnInfo(name = "external_link_id")
    val externalLinkId: Long = 0,

    /**
     * The URL of the external link
     *
     * TODO: use `URL` type instead of raw `String`
     */
    @ColumnInfo(name = "url")
    val url: String = "",

    /** The site location of the external link */
    @ColumnInfo(name = "site")
    val site: String = "",

    /** One-to-many relation foreign key: points to associated media */
    @ColumnInfo(name = "media_id", defaultValue = "-1")
    val mediaId: Long = -1,
) : Parcelable {

    constructor(other: MediaExternalLink, mediaId: Long) : this(
        externalLinkId = other.externalLinkId,
        url = other.url,
        site = other.site,

        mediaId = mediaId,
    )
}
