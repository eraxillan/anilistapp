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
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


/** A tag that describes a theme or element of the media */
@Entity(tableName = "media_tags")
@Parcelize
data class MediaTag constructor(
    /** The id of the tag */
    @PrimaryKey
    @ColumnInfo(name = "tag_id")
    val tagId: Long = -1,

    /** The name of the tag */
    @ColumnInfo(name = "name", defaultValue = "")
    val name: String = "",

    /** A general description of the tag */
    @ColumnInfo(name = "description", defaultValue = "")
    val description: String = "",

    /** The categories of tags this tag belongs to */
    @ColumnInfo(name = "category", defaultValue = "")
    val category: String = "",

    /** The relevance ranking of the tag out of the 100 for this media */
    @ColumnInfo(name = "rank", defaultValue = "-1")
    val rank: Int = -1,

    /** If the tag could be a spoiler for any media */
    @ColumnInfo(name = "is_general_spoiler", defaultValue = "0")
    val isGeneralSpoiler: Boolean = false,

    /** If the tag is a spoiler for this media */
    @ColumnInfo(name = "is_media_spoiler", defaultValue = "0")
    val isMediaSpoiler: Boolean = false,

    /** If the tag is only for adult 18+ media */
    @ColumnInfo(name = "is_adult", defaultValue = "0")
    val isAdult: Boolean = false,
) : Parcelable
