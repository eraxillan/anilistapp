package name.eraxillan.ongoingschedule.ui.holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.R

class OngoingItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val TAG = OngoingItemViewHolder::class.java.simpleName

    val taskTextView = itemView.findViewById(R.id.textview_task) as TextView
}
