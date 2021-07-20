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
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.ui.holder.AiringAnimeDetailHolder

class AiringAnimeDetailAdapter(
    var anime: AiringAnime
)
    : RecyclerView.Adapter<AiringAnimeDetailHolder>() {

    companion object {
        private const val LOG_TAG = "54BE6C87_AADA" // AADA = AiringAnimeDetailAdapter
        private const val fieldCount = 7
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
        val PLACEHOLDER_STRING = "<unknown>"

        val season = if (anime.season != -1) anime.season.toString() else PLACEHOLDER_STRING
        val originalName = if (anime.originalName.isNotEmpty()) anime.originalName else PLACEHOLDER_STRING
        val latestEpisode = if (anime.latestEpisode != -1) anime.latestEpisode.toString() else PLACEHOLDER_STRING
        val totalEpisodes = if (anime.totalEpisodes != -1) anime.totalEpisodes.toString() else PLACEHOLDER_STRING
        val releaseDate = if (anime.releaseDate != null) anime.releaseDate.toString() else PLACEHOLDER_STRING
        val nextEpisodeDate = if (anime.nextEpisodeDate != null) anime.nextEpisodeDate!!.getDisplayString() else PLACEHOLDER_STRING
        val minAge = if (anime.minAge != -1) anime.minAge.toString() else PLACEHOLDER_STRING
        val popularity = if (anime.popularity != -1) anime.popularity else PLACEHOLDER_STRING
        val imageColor = if (anime.imageColor.isNotEmpty()) anime.imageColor else "#000000"

        val text = when (position) {
            0 -> "Season: $season"
            1 -> "Original name: $originalName"
            2 -> "Latest episode: $latestEpisode"
            3 -> "Total episodes: $totalEpisodes"
            4 -> "Release date: $releaseDate"
            5 -> "Next episode date: $nextEpisodeDate"
            6 -> "Minimum age: $minAge"
            7 -> "Popularity: $popularity"
            else -> ""
        }
        holder.bind(text, imageColor.toColorInt())
    }

    // Tells the `RecyclerView` how many items to display
    override fun getItemCount(): Int {
        return fieldCount
    }
}
