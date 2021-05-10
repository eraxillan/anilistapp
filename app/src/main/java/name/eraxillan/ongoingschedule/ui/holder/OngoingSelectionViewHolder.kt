package name.eraxillan.ongoingschedule.ui.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.databinding.ListItemOngoingBinding
import name.eraxillan.ongoingschedule.model.Ongoing

class OngoingSelectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = ListItemOngoingBinding.bind(itemView)

    companion object {
        private val TAG = OngoingSelectionViewHolder::class.java.simpleName
    }

    fun bind(position: Int, ongoing: Ongoing) {
        with (binding) {
            tvOngoingNumber.text = (position + 1).toString()
            tvOngoingName.text = ongoing.originalName
        }
    }
}
