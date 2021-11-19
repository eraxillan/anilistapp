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

package name.eraxillan.anilistapp.data.room.views

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import name.eraxillan.anilistapp.data.room.LocalMedia

@DatabaseView(
    """
    SELECT media_collection.*, media_genres.name AS genre_name FROM media_collection
    INNER JOIN media_genres
    WHERE genre_id IN (SELECT genre_id FROM media_genre_entries WHERE media_id = anilist_id)
    """,
    viewName = "media_with_genres")
class MediaWithGenres {
    @Embedded
    var media: LocalMedia? = null

    @ColumnInfo(name = "genre_name")
    var genreName: String = ""
}

@DatabaseView(
    """
        SELECT media_collection.*, media_tags.name AS tag_name FROM media_collection
        INNER JOIN media_tags
        WHERE tag_id IN (SELECT tag_id FROM media_tag_entries WHERE media_id = anilist_id)
    """,
    viewName = "media_with_tags"
)
class MediaWithTags {
    @Embedded
    var media: LocalMedia? = null

    @ColumnInfo(name = "tag_name")
    var tagName: String = ""
}

@DatabaseView(
    """
        SELECT media_collection.*, media_external_links.site AS service_name FROM media_collection
        INNER JOIN media_external_links ON media_id = anilist_id
    """,
    viewName = "media_with_services"
)
class MediaWithServices {
    @Embedded
    var media: LocalMedia? = null

    @ColumnInfo(name = "service_name")
    var serviceName: String = ""
}
