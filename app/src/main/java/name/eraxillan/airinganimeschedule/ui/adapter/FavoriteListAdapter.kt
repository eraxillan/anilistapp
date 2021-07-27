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
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.airinganimeschedule.databinding.ListItemMediaBinding
import name.eraxillan.airinganimeschedule.model.Media
import name.eraxillan.airinganimeschedule.ui.ItemTouchHelperCallback
import name.eraxillan.airinganimeschedule.ui.holder.MediaListHolder

// TODO: favorite airing anime list adapter with CRUD support (AiringAnimeListAdapter stay read-only)
// `PagingDataAdapter` have read-only data, and there is no way to remove item in-place

class FavoriteListAdapter(
    private val deleteDelegate: (Media) -> Unit
) : RecyclerView.Adapter<MediaListHolder>()
    , ItemTouchHelperCallback.ItemTouchHelperAdapter {

    companion object {
        private const val LOG_TAG = "54BE6C87_FLAd" // FLAd = FavoriteListAdapter
    }

    private val animeList : MutableList<Media> = mutableListOf()

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaListHolder {
        return MediaListHolder(
            ListItemMediaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MediaListHolder, position: Int) {
        // FIXME: load anime image from URL, see Sunflower for example code
        holder.bind(position, animeList[position])
    }

    override fun getItemCount() = animeList.size

    // TODO: uncomment to implement drag-and-drop for list view items
    //override fun onItemMoved(fromPosition: Int, toPosition: Int) = swapItems(fromPosition, toPosition)
    override fun onItemMoved(fromPosition: Int, toPosition: Int) {}

    override fun onItemDismiss(position: Int) = deleteAiringAnime(position)

    // Override to show header/footer
    //override fun getItemViewType(position: Int): Int = 0
    //override fun getItemViewType(position: Int): Int = 0

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun deleteAiringAnime(position: Int) {
        deleteDelegate(animeList[position])
        animeList.removeAt(position)
        notifyItemRemoved(position)
    }

    /*private fun swapItems(positionFrom: Int, positionTo: Int) {
        Collections.swap(animeList, positionFrom, positionTo)
        notifyItemMoved(positionFrom, positionTo)
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun addAiringAnime(anime: Media) {
        animeList.add(anime)
        notifyItemInserted(animeList.size - 1)
    }

    fun deleteAiringAnime(anime: Media) {
        val position = animeList.indexOf(anime)
        animeList.remove(anime)
        notifyItemRemoved(position)
    }

    fun clearAiringAnimeList() {
        animeList.clear()
        notifyDataSetChanged()
    }
}
