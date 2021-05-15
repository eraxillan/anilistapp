package name.eraxillan.ongoingschedule.ui.holder

import android.util.Log
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.databinding.ListItemOngoingBinding
import name.eraxillan.ongoingschedule.model.Ongoing
import name.eraxillan.ongoingschedule.ui.showOngoingInfo

class OngoingSelectionViewHolder(
    private val binding: ListItemOngoingBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        private val LOG_TAG = OngoingSelectionViewHolder::class.java.simpleName
    }

    init {
        binding.setClickListener {
            binding.ongoing?.let { ongoing ->
                showOngoingInfo(ongoing, it.findNavController())
            }
        }
    }

    fun bind(position: Int, ongoing: Ongoing) {
        binding.apply {
            setPosition((position + 1).toString())
            setOngoing(ongoing)
            executePendingBindings()
        }
    }
}
