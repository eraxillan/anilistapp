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

package name.eraxillan.anilistapp.data.room

import name.eraxillan.anilistapp.data.room.relations.MediaGenreEntry
import name.eraxillan.anilistapp.data.room.relations.MediaStudioEntry
import name.eraxillan.anilistapp.data.room.relations.MediaTagEntry
import name.eraxillan.anilistapp.model.*
import timber.log.Timber


private const val FAKE_GENRE = "?"
private const val FAKE_TAG = "?"
private const val FAKE_STUDIO = "?"
//private const val FAKE_SERVICE = "?"
//private const val FAKE_FORMAT = "?"
//private const val FAKE_SOURCE = "?"


class MediaDatabaseHelper(
    private val database: MediaDatabase
) {

    private var _genreCache: MutableMap<String, Long>? = null  // id, name
    private var _tagCache: MutableMap<String, Long>? = null    // id, name
    private var _studioCache: MutableMap<String, Long>? = null // id, name

    private suspend fun genreIdForName(name: String): Long {
        if (_genreCache == null) {
            _genreCache = database.mediaGenreDao().getAll().associate { genre ->
                Pair(genre.name, genre.genreId)
            }.toMutableMap()
            check(_genreCache?.isNotEmpty() == true) { Timber.e("Genre cache is empty!") }
            check(_genreCache?.contains(FAKE_GENRE) == true)
        }

        check(_genreCache != null) { Timber.e("Genre cache was set to null by another thread!") }
        return if (_genreCache?.contains(name) == true) _genreCache?.get(name) ?: -1 else -1
    }

    private suspend fun fakeGenre() = MediaGenre(genreId = genreIdForName(FAKE_GENRE), name = FAKE_GENRE)

    private suspend fun tagIdForName(name: String): Long {
        if (_tagCache == null) {
            _tagCache = database.mediaTagDao().getAll().associate { tag ->
                Pair(tag.name, tag.tagId)
            }.toMutableMap()
            check(_tagCache?.isNotEmpty() == true) { Timber.e("Tag cache is empty!") }
            check(_tagCache?.contains(FAKE_TAG) == true)
        }

        check(_tagCache != null) { Timber.e("Tag cache was set to null by another thread!") }
        return if (_tagCache?.contains(name) == true) _tagCache?.get(name) ?: -1 else -1
    }

    private suspend fun fakeTag() = MediaTag(tagId = tagIdForName(FAKE_TAG), name = FAKE_TAG)

    private suspend fun studioIdForName(name: String): Long {
        if (_studioCache == null) {
            _studioCache = database.mediaStudioDao().getAll().associate { studio ->
                Pair(studio.name, studio.studioId)
            }.toMutableMap()
            check(_studioCache?.isNotEmpty() == true) { Timber.e("Studio cache is empty!") }
            check(_studioCache?.contains(FAKE_STUDIO) == true)
        }

        check(_studioCache != null) { Timber.e("Studio cache was set to null by another thread!") }
        return if (_studioCache?.contains(name) == true) _studioCache?.get(name) ?: -1 else -1
    }

    private suspend fun fakeStudio() = MediaStudio(studioId = studioIdForName(FAKE_STUDIO), name = FAKE_STUDIO)

    suspend fun deleteAllMedias() {
        database.apply {
            // Clear all tables in the database
            remoteKeysDao().clearRemoteKeys()
            mediaDao().deleteAll()

            // Many-to-many tables
            //mediaGenreDao().deleteALl()
            mediaGenreEntryDao().deleteAll()
            //mediaTagDao().deleteAll()
            mediaTagEntryDao().deleteAll()
            //mediaStudioDao().deleteAll()
            mediaStudioEntryDao().deleteAll()

            // One-to-many tables
            mediaTitleSynonymDao().deleteAll()
            mediaExternalLinkDao().deleteAll()
            mediaStreamingEpisodeDao().deleteAll()
            mediaRankingDao().deleteAll()
        }

        Timber.d("Cache database cleared!")
    }

    // Many-to-many tables

    suspend fun insertGenres(genres: List<MediaGenre>, mediaId: Long) {
        // Genres
        // TODO: update genreCache from Anilist/MyAnimeList

        // If the media have no genres, add fake one to simplify subsequent search
        val genresEx = if (genres.isNotEmpty()) genres else listOf(fakeGenre())

        val genresWithMediaId = genresEx.map { genre ->
            var genreId = genreIdForName(genre.name)
            if (genreId == -1L) {
                Timber.w("Tag $genre is absent in cache! Adding...")

                // Insert genre entry
                genreId = database.mediaGenreDao().insert(genre)
                check(genreId != -1L)

                val prev = _genreCache?.put(genre.name, genreId)
                check(prev == null)
            }

            MediaGenreEntry(genreId = genreId, mediaId = mediaId)
        }
        database.mediaGenreEntryDao().insertAll(genresWithMediaId)
    }

    suspend fun insertTags(tags: List<MediaTag>, mediaId: Long) {
        // Tags
        // TODO: update tagCache from Anilist/MyAnimeList

        // If the media have no tags, add fake one to simplify subsequent search
        val tagsEx = if (tags.isNotEmpty()) tags else listOf(fakeTag())

        val tagsWithMediaId = tagsEx.map { tag ->
            var tagId = tagIdForName(tag.name)
            if (tagId == -1L) {
                Timber.w("Tag $tag is absent in cache! Adding...")

                // Insert tag entry
                tagId = database.mediaTagDao().insert(tag)
                check(tagId != -1L)

                val prev = _tagCache?.put(tag.name, tagId)
                check(prev == null)
            }

            MediaTagEntry(tagId = tagId, mediaId = mediaId)
        }
        database.mediaTagEntryDao().insertAll(tagsWithMediaId)
    }

    suspend fun insertStudios(studios: List<MediaStudio>, mediaId: Long) {
        // Studios
        // TODO: update studioCache from Anilist/MyAnimeList

        // If the media have no studios, add fake one to simplify subsequent search
        val studiosEx = if (studios.isNotEmpty()) studios else listOf(fakeStudio())

        val studiosWithMediaId = studiosEx.map { studio ->
            var studioId = studioIdForName(studio.name)
            if (studioId == -1L) {
                Timber.w("Studio $studio is absent in cache! Adding...")

                // Insert new studio entry
                studioId = database.mediaStudioDao().insert(studio)
                check(studioId != -1L)

                val prev = _studioCache?.put(studio.name, studioId)
                check(prev == null)
            }

            MediaStudioEntry(studioId = studioId, mediaId = mediaId)
        }
        database.mediaStudioEntryDao().insertAll(studiosWithMediaId)
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
            check(rank.type != MediaRankingType.UNKNOWN) { Timber.e("rank.type == UNKNOWN") }
            check(rank.format != MediaFormatEnum.UNKNOWN) { Timber.e("rank.format == UNKNOWN") }
            //check(rank.year != -1) { Timber.e("rank.year == -1") }
            //check(rank.season != MediaSeason.UNKNOWN) { Timber.e("rank.season == UNKNOWN") }
            //check(rank.allTime != null)  { Timber.e("rank.allTime == null") }
            check(rank.context.isNotEmpty()) { Timber.e("rank.context == \"\"") }
            check(rank.mediaId == -1L) { Timber.e("rank.mediaId != -1") }

            MediaRank(rank, mediaId)
        }
        database.mediaRankingDao().insertAll(rankingsWithMediaId)
    }
}
