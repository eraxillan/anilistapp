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


@Entity(tableName = "media_studios")
@Parcelize
data class MediaStudio constructor(
    /** The unique identifier of the studio **/
    @PrimaryKey
    @ColumnInfo(name = "studio_id")
    val studioId: Long = 0,

    /** The name of the studio */
    @ColumnInfo(name = "name", defaultValue = "")
    val name: String = "",

    /** Whether the studio is an animation studio or a different kind of company */
    @ColumnInfo(name = "is_animation_studio", defaultValue = "0")
    val isAnimationStudio: Boolean = false,

    /** The media the studio has worked on */
    /*@Ignore*/
    /*val medias: List<LocalMedia> = emptyList(),*/

    /** The URL for the studio page on the AniList website */
    // TODO: use URL class instead of raw `String`
    @ColumnInfo(name = "site_url", defaultValue = "")
    val siteUrl: String = "",

    /** If the studio is marked as favourite by the currently authenticated user */
    /*@ColumnInfo(name = "is_favorite", defaultValue = "0")*/
    /*val isFavorite: Boolean = false,*/

    /** The amount of user's who have added the studio to favorites */
    @ColumnInfo(name = "favorites", defaultValue = "-1")
    val favorites: Int = -1,
) : Parcelable
