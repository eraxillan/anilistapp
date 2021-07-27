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
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import name.eraxillan.airinganimeschedule.databinding.ListItemAiringAnimeBinding
import name.eraxillan.airinganimeschedule.model.Media
import name.eraxillan.airinganimeschedule.ui.holder.AiringAnimeListHolder

class MediaListAdapter
    : PagingDataAdapter<Media, AiringAnimeListHolder>(AiringAnimeDiffCallback()) {

    companion object {
        private const val LOG_TAG = "54BE6C87_AALAd" // AALAd = AiringAnimeListAdapter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AiringAnimeListHolder {
        return AiringAnimeListHolder(
            ListItemAiringAnimeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AiringAnimeListHolder, position: Int) {
        val anime = getItem(position)
        anime?.let { holder.bind(position, anime) }
    }
}

class AiringAnimeDiffCallback : DiffUtil.ItemCallback<Media>() {
    override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
        return oldItem.anilistId == newItem.anilistId
    }

    override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
        return oldItem == newItem
    }
}
