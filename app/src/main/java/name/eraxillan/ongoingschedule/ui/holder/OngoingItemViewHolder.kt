package name.eraxillan.ongoingschedule.ui.holder

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.databinding.ListItemOngoingDetailsBinding

class OngoingItemViewHolder(
    private val binding: ListItemOngoingDetailsBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        private val LOG_TAG = OngoingItemViewHolder::class.java.simpleName
    }

    fun bind(text: String) {
        binding.apply {
            setText(text)
            executePendingBindings()
        }
    }
}
