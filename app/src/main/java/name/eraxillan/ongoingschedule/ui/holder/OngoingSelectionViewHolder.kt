package name.eraxillan.ongoingschedule.ui.holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.R

class OngoingSelectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val TAG = OngoingSelectionViewHolder::class.java.simpleName

    val listPosition = itemView.findViewById(R.id.itemNumber) as TextView
    val listTitle = itemView.findViewById(R.id.itemString) as TextView
}
