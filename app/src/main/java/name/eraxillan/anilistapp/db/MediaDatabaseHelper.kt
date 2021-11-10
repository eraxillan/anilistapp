package name.eraxillan.anilistapp.db

import name.eraxillan.anilistapp.model.*
import timber.log.Timber
import java.lang.AssertionError


class MediaDatabaseHelper(private val database: MediaDatabase) {

    private var _genreCache: Map<String, Long>? = null //id, name
    private var _tagCache: Map<String, Long>? = null // id, name
    // TODO: AniList haven't API for querying the full media studio list, but MAL has.
    //       Right now we populate this cache on the fly while adding new media
    //private var _studioCache: Map<String, Long>? = null // id, name

    private suspend fun genreCache(): Map<String, Long> {
        if (_genreCache == null) {
            _genreCache = database.mediaGenreDao().getAll().associate { genre ->
                Pair(genre.name, genre.genreId)
            }
            check(_genreCache?.isNotEmpty() == true) { Timber.e("genreCache is empty!") }
        }
        return _genreCache ?: throw AssertionError("Set to null by another thread")
    }

    private suspend fun tagCache(): Map<String, Long> {
        if (_tagCache == null) {
            _tagCache = database.mediaTagDao().getAll().associate { tag ->
                Pair(tag.name, tag.tagId)
            }
            check(_tagCache?.isNotEmpty() == true) { Timber.e("tagCache is empty!") }
        }
        return _tagCache ?: throw AssertionError("Set to null by another thread")
    }

    /*
    private suspend fun studioCache(): Map<String, Long> {
        if (_studioCache == null) {
            _studioCache = database.mediaStudioDao().getAll().associate { studio ->
                Pair(studio.name, studio.studioId)
            }
            check(_studioCache?.isNotEmpty() == true) { Timber.e("studioCache is empty!") }
        }
        return _studioCache ?: throw AssertionError("Set to null by another thread")
    }
    */

    private suspend fun insertNotExistingStudios(studios: List<MediaStudio>): List<Long> {
        val studiosWithIds = studios.associateBy { studio -> studio.studioId }
        val existingStudioIds = database.mediaStudioDao().getExistingIds(studiosWithIds.keys.toList())
        val absentStudioIds = studiosWithIds.keys.minus(existingStudioIds)
        val absentStudios = studiosWithIds.filterKeys { absentStudioIds.contains(it) }
        val addedStudioIds = database.mediaStudioDao().insertAll(absentStudios.values.toList())
        return existingStudioIds.toSet().union(addedStudioIds).toList()
    }

    suspend fun deleteAllMedias() {
        // Clear all tables in the database
        database.remoteKeysDao().clearRemoteKeys()
        database.mediaDao().deleteAll()

        // Many-to-many tables
        //database.mediaGenreDao().deleteALl()
        database.mediaGenreEntryDao().deleteAll()
        //database.mediaTagDao().deleteAll()
        database.mediaTagEntryDao().deleteAll()
        database.mediaStudioDao().deleteAll()
        database.mediaStudioEntryDao().deleteAll()

        // One-to-many tables
        database.mediaTitleSynonymDao().deleteAll()
        database.mediaExternalLinkDao().deleteAll()
        database.mediaStreamingEpisodeDao().deleteAll()
        database.mediaRankDao().deleteAll()

        Timber.d("Cache database cleared!")
    }

    // Many-to-many tables

    suspend fun insertGenres(genres: List<MediaGenre>, mediaId: Long) {
        // Genres
        // TODO: update genreCache from Anilist
        val genresWithMediaId = genres.map { genre ->
            check(genre.genreId == 0L) { Timber.e("genre.genreId != 0") }
            check(genre.name.isNotEmpty()) { Timber.e("genre.name == \"\"") }

            check(genreCache().contains(genre.name)) {
                Timber.e("genreCache does not contain genre '${genre.name}'!")
            }

            //val genreId = database.mediaGenreDao().getGenreWithName(genre.name)?.genreId ?: -1L
            //check(genreId != -1L)

            val genreId = genreCache()[genre.name] ?: -1L
            check(genreId != -1L) { Timber.e("genreId == -1") }
            MediaGenreEntry(genreId = genreId, mediaId = mediaId)
        }
        database.mediaGenreEntryDao().insertAll(genresWithMediaId)
    }

    suspend fun insertTags(tags: List<MediaTag>, mediaId: Long) {
        // Tags
        // TODO: update tagCache from Anilist
        val tagsWithMediaId = tags.map { tag ->
            check(tag.tagId != -1L) { Timber.e("tag.tagId == -1") }
            check(tag.name.isNotEmpty()) { Timber.e("tag.name == \"\"") }
            check(tag.description.isNotEmpty()) { Timber.e("tag.description == \"\"") }
            check(tag.category.isNotEmpty()) { Timber.e("tag.category == \"\"") }
            check(tag.rank != -1) { Timber.e("tag.rank == -1") }

            check(tagCache().contains(tag.name)) {
                Timber.e("tagCache does not contain tag '${tag.name}'!")
            }

            //val tagId = database.mediaTagDao().getTagWithName(tag.name)?.tagId ?: -1L
            //check(tagId != -1L)

            val tagId = tagCache()[tag.name] ?: -1
            check(tagId != -1L) { Timber.e("tagId == -1") }
            MediaTagEntry(tagId = tagId, mediaId = mediaId)
        }
        database.mediaTagEntryDao().insertAll(tagsWithMediaId)
    }

    suspend fun insertStudios(studios: List<MediaStudio>, mediaId: Long) {
        // Studios
        // TODO: implement and update studioCache using MAL studio list
        val studioIds = insertNotExistingStudios(studios)

        val studioEntries = studios.mapIndexed { index, studio ->
            check(studio.studioId != -1L) { Timber.e("studio.studioId == -1") }
            check(studio.name.isNotEmpty()) { Timber.e("studio.name == \"\"") }
            //check(studio.isAnimationStudio != null) { Timber.e("studio.isAnimationStudio == null") }
            check(studio.siteUrl.isNotEmpty()) { Timber.e("studio.siteUrl == \"\"") }
            check(studio.favorites != -1) { Timber.e("studio.favorites == -1") }

            val studioId = studioIds[index]
            check(studioId != -1L) { Timber.e("studioId == -1") }
            MediaStudioEntry(studioId = studioId, mediaId = mediaId)
        }
        database.mediaStudioEntryDao().insertAll(studioEntries)
    }

    // One-to-many tables

    suspend fun insertTitleSynonyms(titleSynonyms: List<MediaTitleSynonym>, mediaId: Long) {
        // Add all title synonyms for current media at once to minimize I/O
        val titleSynonymsWithMediaId = titleSynonyms.map { titleSynonym ->
            check(titleSynonym.titleSynonymId == 0L) { Timber.e("titleSynonym.titleSynonymId != 0") }
            check(titleSynonym.name.isNotEmpty()) { Timber.e("titleSynonym.name == \"\"") }
            check(titleSynonym.mediaId == -1L) { Timber.e("titleSynonym.mediaId != -1") }

            MediaTitleSynonym(titleSynonym, mediaId)
        }
        database.mediaTitleSynonymDao().insertAll(titleSynonymsWithMediaId)
    }

    suspend fun insertExternalLinks(externalLinks: List<MediaExternalLink>, mediaId: Long) {
        // Add all external links for current media at once to minimize I/O
        val externalLinksWithMediaId = externalLinks.map { externalLink ->
            check(externalLink.externalLinkId != -1L) { Timber.e("externalLink.externalLinkId == -1") }
            check(externalLink.url.isNotEmpty()) { Timber.e("externalLink.url == \"\"") }
            check(externalLink.site.isNotEmpty()) { Timber.e("externalLink.site == \"\"") }
            check(externalLink.mediaId == -1L) { Timber.e("externalLink.mediaId != -1") }

            MediaExternalLink(externalLink, mediaId)
        }
        database.mediaExternalLinkDao().insertAll(externalLinksWithMediaId)
    }

    suspend fun insertStreamingEpisodes(streamingEpisodes: List<MediaStreamingEpisode>, mediaId: Long) {
        // Add all streaming episodes for current media at once to minimize I/O
        val streamingEpisodesWithMediaId = streamingEpisodes.map { streamingEpisode ->
            check(streamingEpisode.streamingEpisodeId == 0L) { Timber.e("streamingEpisode.streamingEpisodeId != 0") }
            check(streamingEpisode.title.isNotEmpty()) { Timber.e("streamingEpisode.title == \"\"") }
            check(streamingEpisode.thumbnail.isNotEmpty()) { Timber.e("streamingEpisode.thumbnail == \"\"") }
            check(streamingEpisode.url.isNotEmpty()) { Timber.e("streamingEpisode.url == \"\"") }
            check(streamingEpisode.site.isNotEmpty()) { Timber.e("streamingEpisode.site == \"\"") }
            check(streamingEpisode.mediaId == -1L) { Timber.e("streamingEpisode.mediaId != -1") }

            MediaStreamingEpisode(streamingEpisode, mediaId)
        }
        database.mediaStreamingEpisodeDao().insertAll(streamingEpisodesWithMediaId)
    }

    suspend fun insertRankings(rankings: List<MediaRank>, mediaId: Long) {
        // Add all rankings for current media at once to minimize I/O
        val rankingsWithMediaId = rankings.map { rank ->
            check(rank.rankId != -1L) { Timber.e("rank.rankId == -1") }
            check(rank.rankValue != -1) { Timber.e("rank.rankValue == -1") }
            check(rank.type != MediaRankType.UNKNOWN) { Timber.e("rank.type == UNKNOWN") }
            check(rank.format != MediaFormatEnum.UNKNOWN) { Timber.e("rank.format == UNKNOWN") }
            //check(rank.year != -1) { Timber.e("rank.year == -1") }
            //check(rank.season != MediaSeason.UNKNOWN) { Timber.e("rank.season == UNKNOWN") }
            //check(rank.allTime != null)  { Timber.e("rank.allTime == null") }
            check(rank.context.isNotEmpty()) { Timber.e("rank.context == \"\"") }
            check(rank.mediaId == -1L) { Timber.e("rank.mediaId != -1") }

            MediaRank(rank, mediaId)
        }
        database.mediaRankDao().insertAll(rankingsWithMediaId)
    }
}
