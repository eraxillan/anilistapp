package name.eraxillan.airinganimeschedule.ui.holder

import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.airinganimeschedule.databinding.ListItemAiringAnimeBinding
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.ui.showAiringAnimeInfo

class AiringAnimeListHolder(
    private val binding: ListItemAiringAnimeBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        private const val LOG_TAG = "54BE6C87_AALH" // AALH = AiringAnimeListHolder
    }

    init {
        binding.setClickListener {
            binding.anime?.let { anime ->
                showAiringAnimeInfo(anime, it.findNavController())
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
