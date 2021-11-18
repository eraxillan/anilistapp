package name.eraxillan.anilistapp.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import name.eraxillan.anilistapp.model.*


/** An anime/manga detailed information from local database,
 * which should be previously filled from Anilist ([Media])
 * */
data class LocalMedia(
    @Embedded
    val media: Media = Media(),

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
    ) {

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
            media: '${media.romajiTitle}',
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
