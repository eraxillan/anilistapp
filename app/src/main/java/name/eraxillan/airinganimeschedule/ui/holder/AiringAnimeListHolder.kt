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
        private val LOG_TAG = AiringAnimeListHolder::class.java.simpleName
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
