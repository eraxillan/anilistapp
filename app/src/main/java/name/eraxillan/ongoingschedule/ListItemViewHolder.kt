package name.eraxillan.ongoingschedule

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val TAG = ListItemViewHolder::class.java.simpleName

    val taskTextView = itemView.findViewById(R.id.txt_task) as TextView
}
