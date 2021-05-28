package name.eraxillan.ongoingschedule.ui.holder

import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.databinding.ListItemAiringAnimeBinding
import name.eraxillan.ongoingschedule.model.AiringAnime
import name.eraxillan.ongoingschedule.ui.showOngoingInfo

class OngoingSelectionViewHolder(
    private val binding: ListItemAiringAnimeBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        private val LOG_TAG = OngoingSelectionViewHolder::class.java.simpleName
    }

    init {
        binding.setClickListener {
            binding.anime?.let { anime ->
                showOngoingInfo(anime, it.findNavController())
            }
        }
    }

    fun bind(position: Int, anime: AiringAnime) {
        binding.apply {
            setPosition((position + 1).toString())
            setAnime(anime)
            executePendingBindings()
        }
    }
}
