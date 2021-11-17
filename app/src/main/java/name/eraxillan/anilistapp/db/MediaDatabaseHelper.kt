package name.eraxillan.anilistapp.db

import name.eraxillan.anilistapp.model.*
import timber.log.Timber

private const val FAKE_GENRE = "?"
private const val FAKE_TAG = "?"
//private const val FAKE_SERVICE = "?"

class MediaDatabaseHelper(private val database: MediaDatabase) {

    private var _genreCache: Map<String, Long>? = null //id, name
    private var _tagCache: Map<String, Long>? = null // id, name
    // TODO: AniList haven't API for querying the full media studio list, but MAL has.
    //       Right now we populate this cache on the fly while adding new media
    //private var _studioCache: Map<String, Long>? = null // id, name

    private suspend fun genreIdForName(name: String): Long {
        if (_genreCache == null) {
            _genreCache = database.mediaGenreDao().getAll().associate { genre ->
                Pair(genre.name, genre.genreId)
            }
            check(_genreCache?.isNotEmpty() == true) { Timber.e("Genre cache is empty!") }
            check(_genreCache?.contains(FAKE_GENRE) == true)
        }
        check(_genreCache != null) { Timber.e("Genre cache was set to null by another thread!") }

        check(_genreCache?.contains(name) == true) { Timber.e("Genre cache does not contain genre '$name'!") }
        return _genreCache?.get(name) ?: -1
    }

    private suspend fun fakeGenre() = MediaGenre(genreId = genreIdForName(FAKE_GENRE), name = FAKE_GENRE)

    private suspend fun tagIdForName(name: String): Long {
        if (_tagCache == null) {
            _tagCache = database.mediaTagDao().getAll().associate { tag ->
                Pair(tag.name, tag.tagId)
            }
            check(_tagCache?.isNotEmpty() == true) { Timber.e("Tag cache is empty!") }
            check(_tagCache?.contains(FAKE_TAG) == true)
        }
        check(_tagCache != null) { Timber.e("Tag cache was set to null by another thread!") }

        check(_tagCache?.contains(name) == true) { Timber.e("Tag cache does not contain genre '$name'!") }
        return _tagCache?.get(name) ?: -1
    }

    private suspend fun fakeTag() = MediaTag(tagId = tagIdForName(FAKE_GENRE), name = FAKE_TAG)

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

        // If the media have no genres, add fake one to simplify subsequent search
        val genresEx = if (genres.isNotEmpty()) genres else listOf(fakeGenre())

        val genresWithMediaId = genresEx.map { genre ->
            //val genreId = database.mediaGenreDao().getGenreWithName(genre.name)?.genreId ?: -1L
            //check(genreId != -1L)

            val genreId = genreIdForName(genre.name)
            check(genreId != -1L) { Timber.e("genreId == -1") }
            MediaGenreEntry(genreId = genreId, mediaId = mediaId)
        }
        database.mediaGenreEntryDao().insertAll(genresWithMediaId)
    }

    suspend fun insertTags(tags: List<MediaTag>, mediaId: Long) {
        // Tags
        // TODO: update tagCache from Anilist

        // If the media have no tags, add fake one to simplify subsequent search
        val tagsEx = if (tags.isNotEmpty()) tags else listOf(fakeTag())

        val tagsWithMediaId = tagsEx.map { tag ->
            //val tagId = database.mediaTagDao().getTagWithName(tag.name)?.tagId ?: -1L
            //check(tagId != -1L)

            val tagId = tagIdForName(tag.name)
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

    // Include streaming services
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
