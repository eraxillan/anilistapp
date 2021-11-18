package name.eraxillan.anilistapp.data

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import name.eraxillan.anilistapp.model.Media

@DatabaseView(
    """
    SELECT media_collection.*, media_genres.name AS genre_name FROM media_collection
    INNER JOIN media_genres
    WHERE genre_id IN (SELECT genre_id FROM media_genre_entries WHERE media_id = anilist_id)
    """,
    viewName = "media_with_genres")
class MediaWithGenres {
    @Embedded
    var media: Media? = null

    @ColumnInfo(name = "genre_name")
    var genreName: String = ""

    override fun toString(): String {
        return "{ '${media?.romajiTitle}' + '${media?.genres}' }"
    }
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
    var media: Media? = null

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
    var media: Media? = null

    @ColumnInfo(name = "service_name")
    var serviceName: String = ""
}
