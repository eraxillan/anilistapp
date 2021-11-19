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

package name.eraxillan.anilistapp.data.room.relations

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.parcelize.Parcelize
import name.eraxillan.anilistapp.data.room.LocalMedia
import name.eraxillan.anilistapp.model.MediaGenre

/** Media and media genre junction table to decompose many-to-many relation */
@Entity(
    tableName = "media_genre_entries",
    primaryKeys = ["media_id", "genre_id"],
    foreignKeys = [
        ForeignKey(
            entity = LocalMedia::class,
            parentColumns = ["anilist_id"],
            childColumns = ["media_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
            // TODO: need testing
            //deferred = true
        ),
        ForeignKey(
            entity = MediaGenre::class,
            parentColumns = ["genre_id"],
            childColumns = ["genre_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
            // TODO: need testing
            //deferred = true
        ),
    ],
    // TODO: need testing
    indices = [
        Index("media_id", "genre_id", unique = true),
        Index("media_id"),
        Index("genre_id")
    ]
)
@Parcelize
data class MediaGenreEntry(
    //@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "media_id") val mediaId: Long = -1,
    @ColumnInfo(name = "genre_id") val genreId: Long = -1,
) : Parcelable
