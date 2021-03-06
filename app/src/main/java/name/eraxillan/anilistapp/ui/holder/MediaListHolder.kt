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

package name.eraxillan.anilistapp.ui.holder

import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.anilistapp.databinding.ListItemMediaBinding
import name.eraxillan.anilistapp.data.room.LocalMediaWithRelations
import name.eraxillan.anilistapp.ui.showMediaInfo

class MediaListHolder(
    private val binding: ListItemMediaBinding
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.setClickListener { view ->
            binding.media?.let { media ->
                showMediaInfo(media, view.findNavController())
            }
        }
    }

    fun bind(position: Int, media: LocalMediaWithRelations) {
        binding.apply {
            setPosition((position + 1).toString())
            setMedia(media)
            executePendingBindings()
        }
    }
}
