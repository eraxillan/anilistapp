package name.eraxillan.ongoingschedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class OngoingSelectionRecyclerViewAdapter(
    private val lists : ArrayList<TaskList>,
    private val clickListener: ListSelectionRecyclerViewClickListener)
    : RecyclerView.Adapter<OngoingSelectionViewHolder>() {

    private val TAG = OngoingSelectionRecyclerViewAdapter::class.java.simpleName

    interface ListSelectionRecyclerViewClickListener {
        fun listItemClicked(list: TaskList)
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
        holder.listTitle.text = lists[position].name
        holder.itemView.setOnClickListener {
            clickListener.listItemClicked(lists[position])
        }
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun addList(list: TaskList) {
        lists.add(list)
        notifyItemInserted(lists.size - 1)
    }
}
