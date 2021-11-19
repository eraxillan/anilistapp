package name.eraxillan.anilistapp.data.room

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import kotlinx.parcelize.Parcelize
import name.eraxillan.anilistapp.data.room.relations.MediaGenreEntry
import name.eraxillan.anilistapp.data.room.relations.MediaStudioEntry
import name.eraxillan.anilistapp.data.room.relations.MediaTagEntry
import name.eraxillan.anilistapp.model.*

/**
 * An anime/manga detailed information from local database,
 * including one-to-one and one-to-many compound fields like genres, tags, etc.
 * which was originally obtained from remote service.
 *
 * Ideally only [RemoteMedia] and [LocalMedia] can do the job,
 * but Room library an additional artificial class like this to teach it
 * how to join several SQL tables together
 */
@Parcelize
data class LocalMediaWithRelations(
    @Embedded
    val localMedia: LocalMedia = LocalMedia(),

    // Many-to-many relations

    @Relation(
        parentColumn = "anilist_id",
        entity = MediaGenre::class,
        entityColumn = "genre_id",
        associateBy = Junction(
            value = MediaGenreEntry::class,
            parentColumn = "media_id",
            entityColumn = "genre_id"
        )
    )
    val genres: List<MediaGenre> = emptyList(),

    @Relation(
        parentColumn = "anilist_id",
        entity = MediaTag::class,
        entityColumn = "tag_id",
        associateBy = Junction(
            value = MediaTagEntry::class,
            parentColumn = "media_id",
            entityColumn = "tag_id"
        )
    )
    val tags: List<MediaTag> = emptyList(),

    @Relation(
        parentColumn = "anilist_id",
        entity = MediaStudio::class,
        entityColumn = "studio_id",
        associateBy = Junction(
            value = MediaStudioEntry::class,
            parentColumn = "media_id",
            entityColumn = "studio_id"
        )
    )
    val studios: List<MediaStudio> = emptyList(),

    // One-to-many relations

    @Relation(
        parentColumn = "anilist_id",
        entity = MediaTitleSynonym::class,
        entityColumn = "media_id"
    )
    val titleSynonyms: List<MediaTitleSynonym> = emptyList(),

    @Relation(
        parentColumn = "anilist_id",
        entity = MediaExternalLink::class,
        entityColumn = "media_id"
    )
    val externalLinks: List<MediaExternalLink> = emptyList(),

    @Relation(
        parentColumn = "anilist_id",
        entity = MediaStreamingEpisode::class,
        entityColumn = "media_id"
    )
    val streamingEpisodes: List<MediaStreamingEpisode>,

    @Relation(
        parentColumn = "anilist_id",
        entity = MediaRank::class,
        entityColumn = "media_id"
    )
    val rankings: List<MediaRank>,

) : Parcelable {

    override fun toString(): String {
        val genresStr = genres.joinToString(", ") { mediaGenre -> mediaGenre.name }
        val tagsStr = tags.joinToString(", ") { mediaTag -> mediaTag.name }
        val studiosStr = studios.joinToString(", ") { mediaStudio -> mediaStudio.name }
        val synonymsStr = titleSynonyms.joinToString(", ") { synonym -> synonym.name }
        val linksStr = externalLinks.joinToString(", ") { link -> link.site + ": " + link.url }
        val episodesStr = streamingEpisodes.joinToString(", ") { episode -> "'${episode.title}'" }
        val rankingsStr = rankings.joinToString(", ") { rank -> rank.context + ": " + rank.rankValue }
        return """
            {
            localMedia: '${localMedia.romajiTitle}',
            genres: [$genresStr],
            tags: [$tagsStr],
            studios: [$studiosStr],
            synonyms: [$synonymsStr],
            externalLinks: [$linksStr],
            streamingEpisodes: [$episodesStr],
            rankings: [$rankingsStr]
            }
        """.trimIndent()
    }
}
