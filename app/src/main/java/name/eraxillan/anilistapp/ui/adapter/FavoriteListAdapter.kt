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
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.anilistapp.databinding.ListItemMediaBinding
import name.eraxillan.anilistapp.model.Media
import name.eraxillan.anilistapp.ui.ItemTouchHelperCallback
import name.eraxillan.anilistapp.ui.holder.MediaListHolder

// TODO: favorite media list adapter with CRUD support (MediaListAdapter will stay read-only)
// `PagingDataAdapter` have read-only data, and there is no way to remove item in-place

class FavoriteListAdapter(
    private val deleteDelegate: (Media) -> Unit
) : RecyclerView.Adapter<MediaListHolder>()
    , ItemTouchHelperCallback.ItemTouchHelperAdapter {

    private val mediaList : MutableList<Media> = mutableListOf()

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaListHolder {
        return MediaListHolder(
            ListItemMediaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MediaListHolder, position: Int) {
        holder.bind(position, mediaList[position])
    }

    override fun getItemCount() = mediaList.size

    // TODO: uncomment to implement drag-and-drop for list view items
    //override fun onItemMoved(fromPosition: Int, toPosition: Int) = swapItems(fromPosition, toPosition)
    override fun onItemMoved(fromPosition: Int, toPosition: Int) {}

    override fun onItemDismiss(position: Int) = deleteMedia(position)

    // Override to show header/footer
    //override fun getItemViewType(position: Int): Int = 0
    //override fun getItemViewType(position: Int): Int = 0

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun deleteMedia(position: Int) {
        deleteDelegate(mediaList[position])
        mediaList.removeAt(position)
        notifyItemRemoved(position)
    }

    /*private fun swapItems(positionFrom: Int, positionTo: Int) {
        Collections.swap(mediaList, positionFrom, positionTo)
        notifyItemMoved(positionFrom, positionTo)
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun addMedia(media: Media) {
        mediaList.add(media)
        notifyItemInserted(mediaList.size - 1)
    }

    fun deleteMedia(media: Media) {
        val position = mediaList.indexOf(media)
        mediaList.remove(media)
        notifyItemRemoved(position)
    }

    fun clearMediaList() {
        mediaList.clear()
        notifyDataSetChanged()
    }
}
