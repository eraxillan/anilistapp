package name.eraxillan.ongoingschedule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.R
import name.eraxillan.ongoingschedule.model.Ongoing
import name.eraxillan.ongoingschedule.ui.holder.OngoingSelectionViewHolder

class OngoingSelectionRecyclerViewAdapter(
    private val ongoings : ArrayList<Ongoing>,
    private val clickListener: OngoingSelectionRecyclerViewClickListener
)
    : RecyclerView.Adapter<OngoingSelectionViewHolder>() {

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
        holder.listPosition.text = (position + 1).toString()
        holder.listTitle.text = ongoings[position].originalName
        holder.itemView.setOnClickListener {
            clickListener.ongoingClicked(ongoings[position])
        }
    }

    override fun getItemCount(): Int {
        return ongoings.size
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun addOngoing(ongoing: Ongoing) {
        ongoings.add(ongoing)
        notifyItemInserted(ongoings.size - 1)
    }

    fun clearOngoings() {
        val size = ongoings.size
        ongoings.clear()
        notifyItemRangeRemoved(0, size)
    }
}
