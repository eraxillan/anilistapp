package name.eraxillan.ongoingschedule

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OngoingItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val TAG = OngoingItemViewHolder::class.java.simpleName

    val taskTextView = itemView.findViewById(R.id.textview_task) as TextView
}
