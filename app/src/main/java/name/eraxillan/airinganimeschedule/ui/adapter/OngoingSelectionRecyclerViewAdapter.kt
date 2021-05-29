package name.eraxillan.airinganimeschedule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.airinganimeschedule.databinding.ListItemAiringAnimeBinding
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.ui.ItemTouchHelperCallback
import name.eraxillan.airinganimeschedule.ui.holder.OngoingSelectionViewHolder

class OngoingSelectionRecyclerViewAdapter(
    private val deleteDelegate: (AiringAnime) -> Unit
)
    : RecyclerView.Adapter<OngoingSelectionViewHolder>()
    , ItemTouchHelperCallback.ItemTouchHelperAdapter {

    companion object {
        private val LOG_TAG = OngoingSelectionRecyclerViewAdapter::class.java.simpleName
    }

    private val animeList : MutableList<AiringAnime> = mutableListOf()

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingSelectionViewHolder {
        return OngoingSelectionViewHolder(
            ListItemAiringAnimeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: OngoingSelectionViewHolder, position: Int) {
        holder.bind(position, animeList[position])
    }

    override fun getItemCount(): Int {
        return animeList.size
    }

    // TODO: uncomment to implement drag-and-drop for list view items
    //override fun onItemMoved(fromPosition: Int, toPosition: Int) = swapItems(fromPosition, toPosition)
    override fun onItemMoved(fromPosition: Int, toPosition: Int) {}

    override fun onItemDismiss(position: Int) = deleteOngoing(position)

    // Override to show header/footer
    //override fun getItemViewType(position: Int): Int = 0
    //override fun getItemViewType(position: Int): Int = 0

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun deleteOngoing(position: Int) {
        deleteDelegate(animeList[position])
        animeList.removeAt(position)
        notifyItemRemoved(position)
    }

    /*private fun swapItems(positionFrom: Int, positionTo: Int) {
        Collections.swap(animeList, positionFrom, positionTo)
        notifyItemMoved(positionFrom, positionTo)
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun addOngoing(anime: AiringAnime) {
        animeList.add(anime)
        notifyItemInserted(animeList.size - 1)
    }

    fun deleteOngoing(anime: AiringAnime) {
        val position = animeList.indexOf(anime)
        animeList.remove(anime)
        notifyItemRemoved(position)
    }

    fun clearOngoings() {
        animeList.clear()
        notifyDataSetChanged()
    }
}
