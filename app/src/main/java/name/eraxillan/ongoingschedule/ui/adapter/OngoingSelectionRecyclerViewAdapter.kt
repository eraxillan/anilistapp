package name.eraxillan.ongoingschedule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.R
import name.eraxillan.ongoingschedule.model.Ongoing
import name.eraxillan.ongoingschedule.ui.ItemTouchHelperCallback
import name.eraxillan.ongoingschedule.ui.holder.OngoingSelectionViewHolder
import java.util.*
import kotlin.collections.ArrayList

class OngoingSelectionRecyclerViewAdapter(
    private val ongoings : ArrayList<Ongoing>,
    private val clickListener: OngoingSelectionRecyclerViewClickListener
)
    : RecyclerView.Adapter<OngoingSelectionViewHolder>()
    , ItemTouchHelperCallback.ItemTouchHelperAdapter {

    private val TAG = OngoingSelectionRecyclerViewAdapter::class.java.simpleName

    interface OngoingSelectionRecyclerViewClickListener {
        fun ongoingClicked(ongoing: Ongoing)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingSelectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.list_selection_view_holder,
                parent,
                false
            )

        return OngoingSelectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OngoingSelectionViewHolder, position: Int) {
        holder.bind(position, ongoings[position])

        holder.itemView.setOnClickListener {
            clickListener.ongoingClicked(ongoings[position])
        }
    }

    override fun getItemCount(): Int {
        return ongoings.size
    }

    // TODO: uncomment to implement drag-and-drop for list view items
    //override fun onItemMoved(fromPosition: Int, toPosition: Int) = swapItems(fromPosition, toPosition)
    override fun onItemMoved(fromPosition: Int, toPosition: Int) {}

    override fun onItemDismiss(position: Int) = deleteOngoing(position)

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private fun deleteOngoing(position: Int) {
        ongoings.removeAt(position)
        notifyItemRemoved(position)
    }

    /*private fun swapItems(positionFrom: Int, positionTo: Int) {
        Collections.swap(ongoings, positionFrom, positionTo)
        notifyItemMoved(positionFrom, positionTo)
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun addOngoing(ongoing: Ongoing) {
        ongoings.add(ongoing)
        notifyItemInserted(ongoings.size - 1)
    }

    fun deleteOngoing(ongoing: Ongoing) {
        val position = ongoings.indexOf(ongoing)
        ongoings.remove(ongoing)
        notifyItemRemoved(position)
    }

    fun clearOngoings() {
        ongoings.clear()
        notifyDataSetChanged()
    }
}
