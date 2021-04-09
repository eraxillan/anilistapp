package name.eraxillan.ongoingschedule

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OngoingSelectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val TAG = OngoingSelectionViewHolder::class.java.simpleName

    val listPosition = itemView.findViewById(R.id.itemNumber) as TextView
    val listTitle = itemView.findViewById(R.id.itemString) as TextView
}