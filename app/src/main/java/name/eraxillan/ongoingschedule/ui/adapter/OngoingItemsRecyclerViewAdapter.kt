package name.eraxillan.ongoingschedule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.R
import name.eraxillan.ongoingschedule.TaskList
import name.eraxillan.ongoingschedule.ui.holder.OngoingItemViewHolder

class OngoingItemsRecyclerViewAdapter(var list: TaskList)
    : RecyclerView.Adapter<OngoingItemViewHolder>() {

    private val TAG = OngoingItemsRecyclerViewAdapter::class.java.simpleName

    // Create a `View` from the `Layout`
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.task_view_holder,
                parent,
                false
            )
        return OngoingItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OngoingItemViewHolder, position: Int) {
        holder.taskTextView.text = list.tasks[position]
    }

    // Tells the `RecyclerView` how many items to display
    override fun getItemCount(): Int {
        return list.tasks.size
    }
}