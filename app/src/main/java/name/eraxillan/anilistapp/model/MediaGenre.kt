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

/** A genre of the media */
@Entity(tableName = "media_genres")
@Parcelize
data class MediaGenre constructor(
    /** The id of the genre */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "genre_id")
    val genreId: Long = 0,

    /** The name of the genre */
    @ColumnInfo(name = "name", defaultValue = "")
    val name: String = "",

    /** If the tag is only for adult 18+ media */
    @ColumnInfo(name = "is_adult", defaultValue = "0")
    val isAdult: Boolean = false,
) : Parcelable
