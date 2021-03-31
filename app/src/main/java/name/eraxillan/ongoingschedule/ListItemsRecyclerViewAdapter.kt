package name.eraxillan.ongoingschedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ListItemsRecyclerViewAdapter(var list: TaskList)
    : RecyclerView.Adapter<ListItemViewHolder>() {

    private val TAG = ListItemsRecyclerViewAdapter::class.java.simpleName

    // Create a `View` from the `Layout`
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.task_view_holder,
                parent,
                false
            )
        return ListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        holder.taskTextView.text = list.tasks[position]
    }

    // Tells the `RecyclerView` how many items to display
    override fun getItemCount(): Int {
        return list.tasks.size
    }
}