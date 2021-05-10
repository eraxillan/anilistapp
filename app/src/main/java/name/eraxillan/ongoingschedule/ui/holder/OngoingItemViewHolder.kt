package name.eraxillan.ongoingschedule.ui.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.databinding.ListItemOngoingDetailsBinding

class OngoingItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = ListItemOngoingDetailsBinding.bind(itemView)

    companion object {
        private val LOG_TAG = OngoingItemViewHolder::class.java.simpleName
    }

    fun bind(text: String) {
        with (binding) {
            tvOngoingInfo.text = text
        }
    }
}
