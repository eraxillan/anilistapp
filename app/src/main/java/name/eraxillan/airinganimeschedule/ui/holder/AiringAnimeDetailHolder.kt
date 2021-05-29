package name.eraxillan.airinganimeschedule.ui.holder

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.airinganimeschedule.databinding.ListItemAiringAnimeDetailBinding

class OngoingItemViewHolder(
    private val binding: ListItemAiringAnimeDetailBinding
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
