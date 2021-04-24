package name.eraxillan.ongoingschedule.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.R
import name.eraxillan.ongoingschedule.model.Ongoing
import name.eraxillan.ongoingschedule.ui.ItemTouchHelperCallback
import name.eraxillan.ongoingschedule.ui.holder.OngoingSelectionViewHolder

class OngoingSelectionRecyclerViewAdapter(
    private val ongoings : MutableList<Ongoing>,
    private val clickListener: OngoingSelectionRecyclerViewClickListener
)
    : RecyclerView.Adapter<OngoingSelectionViewHolder>()
    , ItemTouchHelperCallback.ItemTouchHelperAdapter {

    private val TAG = OngoingSelectionRecyclerViewAdapter::class.java.simpleName

    interface OngoingSelectionRecyclerViewClickListener {
        fun ongoingClicked(ongoing: Ongoing)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingSelectionViewHolder =
        OngoingSelectionViewHolder(parent.inflate(R.layout.view_holder_ongoing_item))

    private fun ViewGroup.inflate(layoutRes: Int): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, false)
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

    // Override to show header/footer
    //override fun getItemViewType(position: Int): Int = 0
    //override fun getItemViewType(position: Int): Int = 0

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
