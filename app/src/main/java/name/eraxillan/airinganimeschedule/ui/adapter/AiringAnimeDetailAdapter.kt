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

package name.eraxillan.airinganimeschedule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.airinganimeschedule.databinding.ListItemAiringAnimeDetailBinding
import name.eraxillan.airinganimeschedule.model.Media
import name.eraxillan.airinganimeschedule.model.MediaStatus
import name.eraxillan.airinganimeschedule.ui.holder.AiringAnimeDetailHolder

class AiringAnimeDetailAdapter(
    var anime: Media
)
    : RecyclerView.Adapter<AiringAnimeDetailHolder>() {

    companion object {
        private const val LOG_TAG = "54BE6C87_AADA" // AADA = AiringAnimeDetailAdapter
        private const val fieldCount = 22
    }

    // Create a `View` from the `Layout`
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AiringAnimeDetailHolder {
        return AiringAnimeDetailHolder(
            ListItemAiringAnimeDetailBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AiringAnimeDetailHolder, position: Int) {
        val PLACEHOLDER_STRING = "?"

        val format = anime.format.toString()
        val episodes = if (anime.episodeCount != -1) anime.episodeCount.toString() else PLACEHOLDER_STRING
        val episodeDuration = if (anime.episodeDuration != -1) anime.episodeDuration.toString() else PLACEHOLDER_STRING
        val status = anime.status.toString()
        val startDate = anime.startDate?.toString() ?: PLACEHOLDER_STRING
        val startSeason = anime.startSeason.toString()
        val startSeasonYear = if (anime.startSeasonYear != -1) anime.startSeasonYear.toString() else PLACEHOLDER_STRING
        val averageScore = if (anime.averageScore != -1) anime.averageScore.toString() else PLACEHOLDER_STRING
        val meanScore = if (anime.meanScore != -1) anime.meanScore.toString() else PLACEHOLDER_STRING
        val popularity = if (anime.popularity != -1) anime.popularity.toString() else PLACEHOLDER_STRING
        val favorites = if (anime.favorites != -1) anime.favorites.toString() else PLACEHOLDER_STRING
        val studios = anime.studios.filter { studio -> studio.isAnimationStudio }.joinToString(", ") { studio -> studio.name }
        val producers = anime.studios.filter { studio -> !studio.isAnimationStudio }.joinToString(", ") { producer -> producer.name }
        val source = anime.source.toString()
        val hashtag = if (anime.hashtag.isNotEmpty()) anime.hashtag else PLACEHOLDER_STRING
        val genres = anime.genres.filter { true }.joinToString(", ") { genre -> genre.genre }
        val romajiTitle = if (anime.romajiTitle.isNotEmpty()) anime.romajiTitle else PLACEHOLDER_STRING
        val englishTitle = if (anime.englishTitle.isNotEmpty()) anime.englishTitle else PLACEHOLDER_STRING
        val nativeTitle = if (anime.nativeTitle.isNotEmpty()) anime.nativeTitle else PLACEHOLDER_STRING
        val synonyms = anime.titleSynonyms.joinToString(", ") { synonym -> synonym.title }

        // Airing anime specific fields
        val nextEpisodeNo = if (anime.nextEpisodeNo != -1) anime.nextEpisodeNo.toString() else PLACEHOLDER_STRING
        val nextEpisodeDate = if (anime.nextEpisodeAiringAt != null) anime.nextEpisodeAiringAt!!.getDisplayString() else PLACEHOLDER_STRING
        // TODO: add remaining time too

        // Our custom fields not present on Anilist
        val season = if (anime.season != -1) anime.season.toString() else PLACEHOLDER_STRING
        val minAge = if (anime.minAge != -1) anime.minAge.toString() else PLACEHOLDER_STRING

        val imageColor = if (anime.coverImageColor.isNotEmpty()) anime.coverImageColor else "#000000"

        // TODO: extract these strings to resources
        val text = when (position) {
            // NOTE: currently only airing anime requested from Anilist;
            // else we need to hide airing-specific data fields
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
        holder.bind(text, imageColor.toColorInt())
    }

    // Tells the `RecyclerView` how many items to display
    override fun getItemCount(): Int {
        return fieldCount
    }
}
