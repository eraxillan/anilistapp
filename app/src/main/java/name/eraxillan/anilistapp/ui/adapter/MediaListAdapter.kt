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
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import name.eraxillan.anilistapp.databinding.ListItemMediaBinding
import name.eraxillan.anilistapp.data.room.LocalMediaWithRelations
import name.eraxillan.anilistapp.ui.holder.MediaListHolder

class MediaListAdapter
    : PagingDataAdapter<LocalMediaWithRelations, MediaListHolder>(MediaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaListHolder {
        return MediaListHolder(
            ListItemMediaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MediaListHolder, position: Int) {
        val media = getItem(position)
        media?.let { holder.bind(position, media) }
    }
}

class MediaDiffCallback : DiffUtil.ItemCallback<LocalMediaWithRelations>() {
    override fun areItemsTheSame(oldItem: LocalMediaWithRelations, newItem: LocalMediaWithRelations): Boolean {
        return oldItem.localMedia.anilistId == newItem.localMedia.anilistId
    }

    override fun areContentsTheSame(oldItem: LocalMediaWithRelations, newItem: LocalMediaWithRelations): Boolean {
        return oldItem == newItem
    }
}
