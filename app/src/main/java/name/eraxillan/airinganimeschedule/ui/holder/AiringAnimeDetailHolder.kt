package name.eraxillan.airinganimeschedule.ui.holder

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.airinganimeschedule.databinding.ListItemAiringAnimeDetailBinding

class AiringAnimeDetailHolder(
    private val binding: ListItemAiringAnimeDetailBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        private const val LOG_TAG = "54BE6C87_AADH" // AADH = AiringAnimeDetailHolder
    }

    fun bind(text: String) {
        binding.apply {
            setText(text)
            executePendingBindings()
        }
    }
}
