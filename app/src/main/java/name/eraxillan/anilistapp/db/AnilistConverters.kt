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

package name.eraxillan.anilistapp.db

import name.eraxillan.anilistapp.AiringAnimeQuery
import name.eraxillan.anilistapp.model.*
import timber.log.Timber
import name.eraxillan.anilistapp.type.MediaFormat as AnilistMediaFormat
import name.eraxillan.anilistapp.type.MediaStatus as AnilistMediaStatus
import name.eraxillan.anilistapp.type.MediaSeason as AnilistMediaSeason
import name.eraxillan.anilistapp.type.MediaSource as AnilistMediaSource
import name.eraxillan.anilistapp.type.MediaRankType as AnilistMediaRankType
import name.eraxillan.anilistapp.type.MediaSort as AnilistMediaSort
import name.eraxillan.anilistapp.model.MediaStatus as MediaStatus
import name.eraxillan.anilistapp.model.MediaRankType as MediaRankType
import name.eraxillan.anilistapp.model.MediaSort as MediaSort
import java.net.URL
import java.time.*


fun convertSortType(value: MediaSort) = when (value) {
    MediaSort.BY_TITLE -> AnilistMediaSort.TITLE_ROMAJI
    MediaSort.BY_POPULARITY -> AnilistMediaSort.POPULARITY_DESC
    MediaSort.BY_AVERAGE_SCORE -> AnilistMediaSort.SCORE_DESC
    MediaSort.BY_TRENDING -> AnilistMediaSort.TRENDING_DESC
    MediaSort.BY_FAVORITES -> AnilistMediaSort.FAVOURITES_DESC
    MediaSort.BY_DATE_ADDED -> AnilistMediaSort.START_DATE
    MediaSort.BY_RELEASE_DATE -> AnilistMediaSort.END_DATE
    else -> AnilistMediaSort.UNKNOWN__
}

fun convertAnilistMedia(input: AiringAnimeQuery.Medium): Media {

    fun convertFormat(value: AnilistMediaFormat?) = when (value) {
        AnilistMediaFormat.TV -> MediaFormatEnum.TV
        AnilistMediaFormat.TV_SHORT -> MediaFormatEnum.TV_SHORT
        AnilistMediaFormat.MOVIE -> MediaFormatEnum.MOVIE
        AnilistMediaFormat.SPECIAL -> MediaFormatEnum.SPECIAL
        AnilistMediaFormat.OVA -> MediaFormatEnum.OVA
        AnilistMediaFormat.ONA -> MediaFormatEnum.ONA
        AnilistMediaFormat.MUSIC -> MediaFormatEnum.MUSIC
        AnilistMediaFormat.MANGA -> MediaFormatEnum.MANGA
        AnilistMediaFormat.NOVEL -> MediaFormatEnum.NOVEL
        AnilistMediaFormat.ONE_SHOT -> MediaFormatEnum.ONE_SHOT
        else -> MediaFormatEnum.UNKNOWN
    }

    fun convertStatus(value: AnilistMediaStatus?) = when (value) {
        AnilistMediaStatus.FINISHED -> MediaStatus.FINISHED
        AnilistMediaStatus.RELEASING -> MediaStatus.RELEASING
        AnilistMediaStatus.NOT_YET_RELEASED -> MediaStatus.NOT_YET_RELEASED
        AnilistMediaStatus.CANCELLED -> MediaStatus.CANCELLED
        AnilistMediaStatus.HIATUS -> MediaStatus.HIATUS
        else -> MediaStatus.UNKNOWN
    }

    fun convertSeason(value: AnilistMediaSeason?) = when (value) {
        AnilistMediaSeason.WINTER -> MediaSeason.WINTER
        AnilistMediaSeason.SPRING -> MediaSeason.SPRING
        AnilistMediaSeason.SUMMER -> MediaSeason.SUMMER
        AnilistMediaSeason.FALL -> MediaSeason.FALL
        else -> MediaSeason.UNKNOWN
    }

    fun convertSource(value: AnilistMediaSource?) = when (value) {
        AnilistMediaSource.MANGA -> MediaSourceEnum.MANGA
        AnilistMediaSource.LIGHT_NOVEL -> MediaSourceEnum.LIGHT_NOVEL
        AnilistMediaSource.VISUAL_NOVEL -> MediaSourceEnum.VISUAL_NOVEL
        AnilistMediaSource.VIDEO_GAME -> MediaSourceEnum.VIDEO_GAME
        AnilistMediaSource.OTHER -> MediaSourceEnum.OTHER
        AnilistMediaSource.NOVEL -> MediaSourceEnum.NOVEL
        AnilistMediaSource.DOUJINSHI -> MediaSourceEnum.DOUJINSHI
        AnilistMediaSource.ANIME -> MediaSourceEnum.ANIME
        else -> MediaSourceEnum.UNKNOWN
    }

    fun convertRankType(value: AnilistMediaRankType?) = when (value) {
        AnilistMediaRankType.RATED -> MediaRankType.RATED
        AnilistMediaRankType.POPULAR -> MediaRankType.POPULAR
        else -> MediaRankType.UNKNOWN
    }

    fun hasAdultTags(): Boolean {
        return input.tags?.any { tag ->
            if (tag?.isAdult == true) {
                Timber.e("Adult tag detected: '${tag.name}'")
                tag.isAdult
            } else false
        } == true
    }

    fun getMinAge(): Int {
        // NOTE: We already filtered strictly 18+ titles in GraphQL query

        // All possible demographic-related tags and corresponding minimal age
        val demographicTagAges = mapOf(
            "kids" to 0,      // Males/Females 0-10
            "shounen" to 10,  // Shounen: Males 10-17
            "shoujo" to 10,   // Shoujo: Females 10-17
            "seinen" to 17,   // Seinen: Males 17+
            "josei" to 17     // Josei: Females 17+
        )

        // First of all, convert all tag names to lowercase to simplify string comparison
        val inputTags = input.tags?.map { it?.name?.lowercase() ?: "" } ?: emptyList()

        // Now found the intersection between predefined demographic tags and input ones
        val intersectionTags = inputTags.intersect(demographicTagAges.keys)

        // Find maximum age available in demographic tags
        val minAgeTag = intersectionTags.maxByOrNull { demographicTagAges[it] ?: 0 }
        var minAge = demographicTagAges[minAgeTag] ?: -1

        // Finally search for adult tags and increase minimal age to 18 if at least one found
        if (hasAdultTags())
            minAge = 18

        return minAge
    }

    val malId = input.idMal?.toLong() ?: -1L
    val malUrl = if (malId != -1L)
        URL("https://myanimelist.net/anime/$malId")
    else INVALID_URL

    // Get currently used time zone and offset
    //Timber.d("Current timezone name is '${TimeZone.getDefault().displayName}'")
    //Timber.d("Current timezone ID is '${TimeZone.getDefault().id}'")
    val zoneId = ZoneId.systemDefault()
    val zoneOffset = LocalDateTime.now().atZone(zoneId).offset

    val updatedTime = LocalDateTime.ofEpochSecond(
        input.updatedAt?.toLong() ?: 0L,
        0, zoneOffset
    )
    val airingTime = LocalDateTime.ofEpochSecond(
        input.nextAiringEpisode?.airingAt?.toLong() ?: 0L,
        0, zoneOffset
    )
    val timeUntilAiring = LocalDateTime.ofEpochSecond(
        input.nextAiringEpisode?.timeUntilAiring?.toLong() ?: 0L,
        0, zoneOffset
    )

    val synonyms = input.synonyms?.filterNotNull()?.map { element ->
        MediaTitleSynonym(name = element) }
        ?: emptyList()

    val genres = input.genres?.filterNotNull()?.map { element ->
        MediaGenre(name = element) }
        ?: emptyList()

    val tags = input.tags?.filterNotNull()?.map { tag ->
        MediaTag(
            tag.id.toLong(), tag.name, tag.description ?: "", tag.category ?: "",
            tag.rank ?: -1, tag.isGeneralSpoiler ?: false,
            tag.isMediaSpoiler ?: false, tag.isAdult ?: false
        )
    } ?: emptyList()

    val studios = input.studios?.edges?.filterNotNull()?.map { studio ->
        MediaStudio(
            studio.node?.id?.toLong() ?: -1L, studio.node?.name ?: "",
            studio.node?.isAnimationStudio ?: false,
            studio.node?.siteUrl ?: "", studio.node?.favourites ?: -1
        )
    } ?: emptyList()

    val externalLinks =
        input.externalLinks?.filterNotNull()?.map { link ->
            MediaExternalLink(link.id.toLong(), link.url, link.site)
        } ?: emptyList()

    val streamingEpisodes = input.streamingEpisodes?.filterNotNull()?.map { se ->
        MediaStreamingEpisode(0,
            se.title ?: "", se.thumbnail ?: "",
            se.url ?: "", se.site ?: ""
        )
    } ?: emptyList()

    val rankings = input.rankings?.filterNotNull()?.map { ranking ->
        MediaRank(
            ranking.id.toLong(),
            ranking.rank,
            convertRankType(ranking.type),
            convertFormat(ranking.format),
            ranking.year ?: -1,
            convertSeason(ranking.season),
            ranking.allTime ?: false,
            ranking.context
        )
    } ?: emptyList()

    val format = convertFormat(input.format)
    val status = convertStatus(input.status)
    val startSeason = convertSeason(input.season)
    val source = convertSource(input.source)

    return Media(
        anilistId = input.id.toLong(),
        malId = malId,
        anilistUrl = URL(input.siteUrl),
        malUrl = malUrl,
        updatedAt = updatedTime,
        romajiTitle = input.title?.romaji ?: "",
        englishTitle = input.title?.english ?: "",
        nativeTitle = input.title?.native_ ?: "",
        titleSynonyms = synonyms,
        format = format,
        status = status,
        description = input.description ?: "",
        startDate = LocalDate.of(
            input.startDate?.year ?: 1970,
            input.startDate?.month ?: 1,
            input.startDate?.day ?: 1
        ),
        endDate = LocalDate.of(
            input.endDate?.year ?: 1970,
            input.endDate?.month ?: 1,
            input.endDate?.day ?: 1
        ),
        startSeason = startSeason,
        startSeasonYear = input.seasonYear ?: -1,
        episodeCount = input.episodes ?: -1,
        episodeDuration = input.duration ?: -1,
        nextEpisodeAiringAt = ZonedScheduledTime.of(
            airingTime.dayOfWeek,
            airingTime.toLocalTime(),
            zoneOffset,
            zoneId
        ),
        nextEpisodeTimeUntilAiring = timeUntilAiring,
        nextEpisodeNo = input.nextAiringEpisode?.episode ?: -1,
        countryOfOrigin = input.countryOfOrigin.toString(),
        isLicensed = input.isLicensed ?: false,
        source = source,
        hashtag = input.hashtag ?: "",
        trailerSite = input.trailer?.site ?: "",
        trailerThumbnail = input.trailer?.thumbnail ?: "",
        coverImageExtraLarge = input.coverImage?.extraLarge ?: "",
        coverImageLarge = input.coverImage?.large ?: "",
        coverImageMedium = input.coverImage?.medium ?: "",
        coverImageColor = input.coverImage?.color ?: "",
        bannerImage = input.bannerImage ?: "",
        genres = genres,
        averageScore = input.averageScore ?: -1,
        meanScore = input.meanScore ?: -1,
        popularity = input.popularity ?: -1,
        favorites = input.favourites ?: -1,
        trending = input.trending ?: -1,
        tags = tags,
        studios = studios,
        externalLinks = externalLinks,
        streamingEpisodes = streamingEpisodes,
        rankings = rankings,

        // Calculated fields which not present in Anilist data
        season = -1, // NOTE: will be determined later in `PagingSource`/`RemoteMediator`
        minAge = getMinAge(),
    )
}

////////////////////////////////////////////////////////////////////////////////////////////////////
// FIXME: avoid extra conversions [Remote]Media <--> LocalMedia

fun convertRemoteMedia(input: Media): LocalMedia {
    return LocalMedia(
        media = input,
        genres = input.genres,
        tags = input.tags,
        studios = input.studios,
        titleSynonyms = input.titleSynonyms,
        externalLinks = input.externalLinks,
        streamingEpisodes = input.streamingEpisodes,
        rankings = input.rankings
    )
}

fun convertLocalMedia(input: LocalMedia): Media {
    return Media(
        anilistId = input.media.anilistId,
        malId = input.media.malId,
        anilistUrl = input.media.anilistUrl,
        malUrl = input.media.malUrl,
        updatedAt = input.media.updatedAt,
        romajiTitle = input.media.romajiTitle,
        englishTitle = input.media.englishTitle,
        nativeTitle = input.media.nativeTitle,
        format = input.media.format,
        status = input.media.status,
        description = input.media.description,
        startDate = input.media.startDate,
        endDate = input.media.endDate,
        startSeason = input.media.startSeason,
        startSeasonYear = input.media.startSeasonYear,
        episodeCount = input.media.episodeCount,
        episodeDuration = input.media.episodeDuration,
        nextEpisodeAiringAt = input.media.nextEpisodeAiringAt,
        nextEpisodeTimeUntilAiring = input.media.nextEpisodeTimeUntilAiring,
        nextEpisodeNo = input.media.nextEpisodeNo,
        countryOfOrigin = input.media.countryOfOrigin,
        isLicensed = input.media.isLicensed,
        source = input.media.source,
        hashtag = input.media.hashtag,
        trailerSite = input.media.trailerSite,
        trailerThumbnail = input.media.trailerThumbnail,
        coverImageExtraLarge = input.media.coverImageExtraLarge,
        coverImageLarge = input.media.coverImageLarge,
        coverImageMedium = input.media.coverImageMedium,
        coverImageColor = input.media.coverImageColor,
        bannerImage = input.media.bannerImage,
        averageScore = input.media.averageScore,
        meanScore = input.media.meanScore,
        popularity = input.media.popularity,
        favorites = input.media.favorites,
        trending = input.media.trending,

        genres = input.genres,
        tags = input.tags,
        studios = input.studios,

        titleSynonyms = input.titleSynonyms,
        externalLinks = input.externalLinks,
        streamingEpisodes = input.streamingEpisodes,
        rankings = input.rankings,

        // Calculated fields which not present in Anilist data
        season = input.media.season,
        minAge = input.media.minAge,
    )
}
