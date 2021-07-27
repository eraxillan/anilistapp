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

package name.eraxillan.airinganimeschedule.ui.holder

import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.airinganimeschedule.databinding.ListItemAiringAnimeBinding
import name.eraxillan.airinganimeschedule.model.Media
import name.eraxillan.airinganimeschedule.ui.showAiringAnimeInfo

class AiringAnimeListHolder(
    private val binding: ListItemAiringAnimeBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        private const val LOG_TAG = "54BE6C87_AALH" // AALH = AiringAnimeListHolder
    }

    init {
        binding.setClickListener { view ->
            binding.media?.let { anime ->
                showAiringAnimeInfo(anime, view.findNavController())
            }
        }
    }

    fun bind(position: Int, media: Media) {
        binding.apply {
            setPosition((position + 1).toString())
            setMedia(media)
            executePendingBindings()
        }
    }
}
