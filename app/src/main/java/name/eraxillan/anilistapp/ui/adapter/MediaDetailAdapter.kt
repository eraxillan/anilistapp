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

package name.eraxillan.anilistapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.anilistapp.databinding.ListItemMediaDetailBinding
import name.eraxillan.anilistapp.model.Media
import name.eraxillan.anilistapp.ui.correctTextColor
import name.eraxillan.anilistapp.ui.holder.MediaDetailHolder

class MediaDetailAdapter(var media: Media, @ColorInt val surfaceColor: Int) : RecyclerView.Adapter<MediaDetailHolder>() {

    companion object {
        private const val fieldCount = 22
    }

    // Create a `View` from the `Layout`
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaDetailHolder {
        return MediaDetailHolder(
            ListItemMediaDetailBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MediaDetailHolder, position: Int) {
        val PLACEHOLDER_STRING = "?"

        val format = media.format.toString()
        val episodes = if (media.episodeCount != -1) media.episodeCount.toString() else PLACEHOLDER_STRING
        val episodeDuration = if (media.episodeDuration != -1) media.episodeDuration.toString() else PLACEHOLDER_STRING
        val status = media.status.toString()
        val startDate = media.startDate?.toString() ?: PLACEHOLDER_STRING
        val startSeason = media.startSeason.toString()
        val startSeasonYear = if (media.startSeasonYear != -1) media.startSeasonYear.toString() else PLACEHOLDER_STRING
        val averageScore = if (media.averageScore != -1) media.averageScore.toString() else PLACEHOLDER_STRING
        val meanScore = if (media.meanScore != -1) media.meanScore.toString() else PLACEHOLDER_STRING
        val popularity = if (media.popularity != -1) media.popularity.toString() else PLACEHOLDER_STRING
        val favorites = if (media.favorites != -1) media.favorites.toString() else PLACEHOLDER_STRING
        val studios = media.studios.filter { studio -> studio.isAnimationStudio }.joinToString(", ") { studio -> studio.name }
        val producers = media.studios.filter { studio -> !studio.isAnimationStudio }.joinToString(", ") { producer -> producer.name }
        val source = media.source.toString()
        val hashtag = if (media.hashtag.isNotEmpty()) media.hashtag else PLACEHOLDER_STRING
        val genres = media.genres.filter { true }.joinToString(", ") { genre -> genre.genre }
        val romajiTitle = if (media.romajiTitle.isNotEmpty()) media.romajiTitle else PLACEHOLDER_STRING
        val englishTitle = if (media.englishTitle.isNotEmpty()) media.englishTitle else PLACEHOLDER_STRING
        val nativeTitle = if (media.nativeTitle.isNotEmpty()) media.nativeTitle else PLACEHOLDER_STRING
        val synonyms = media.titleSynonyms.joinToString(", ") { synonym -> synonym.title }

        // Airing anime specific fields
        val nextEpisodeNo = if (media.nextEpisodeNo != -1) media.nextEpisodeNo.toString() else PLACEHOLDER_STRING
        val nextEpisodeDate = if (media.nextEpisodeAiringAt != null) media.nextEpisodeAiringAt!!.getDisplayString() else PLACEHOLDER_STRING
        // TODO: add remaining time too

        // Our custom fields not present on Anilist
        val season = if (media.season != -1) media.season.toString() else PLACEHOLDER_STRING
        val minAge = if (media.minAge != -1) media.minAge.toString() else PLACEHOLDER_STRING

        val imageColorStr = if (media.coverImageColor.isNotEmpty()) media.coverImageColor else "#000000"

        // TODO: extract these strings to resources
        val text = when (position) {
            // NOTE: currently only airing anime requested from Anilist;
            // else we need to hide airing-specific data fields for non-airing media
            0 -> "Airing\nEpisode $nextEpisodeNo: $nextEpisodeDate"
            1 -> "Format: $format"
            2 -> "Episodes: $episodes"
            3 -> "Episode Duration: $episodeDuration mins"
            4 -> "Status: $status"
            5 -> "Start Date: $startDate"
            6 -> "Season: $startSeason $startSeasonYear"
            7 -> "Average score (weighted): ${averageScore}%"
            8 -> "Mean Score: ${meanScore}%"
            9 -> "Popularity: $popularity"
            10 -> "Favorites: $favorites"
            11 -> "Studios: $studios"
            12 -> "Producers: $producers"
            13 -> "Source: $source"
            14 -> "Hashtag: $hashtag"
            15 -> "Genres: $genres"
            16 -> "Romaji: $romajiTitle"
            17 -> "English: $englishTitle"
            18 -> "Native: $nativeTitle"
            19 -> "Synonyms: $synonyms"
            20 -> "Season number: $season"
            21 -> "Age restrictions: ${minAge}+"
            else -> ""
        }

        // Adopt color for current theme: avoid black on black and white on white
        val imageColor = correctTextColor(imageColorStr.toColorInt(), surfaceColor)

        holder.bind(text, imageColor)
    }

    // Tells the `RecyclerView` how many items to display
    override fun getItemCount(): Int {
        return fieldCount
    }
}
