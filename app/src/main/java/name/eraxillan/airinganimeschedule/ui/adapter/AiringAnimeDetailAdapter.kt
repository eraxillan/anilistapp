package name.eraxillan.airinganimeschedule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.airinganimeschedule.databinding.ListItemAiringAnimeDetailBinding
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.ui.holder.AiringAnimeDetailHolder

class AiringAnimeDetailAdapter(
    var anime: AiringAnime
)
    : RecyclerView.Adapter<AiringAnimeDetailHolder>() {

    companion object {
        private const val LOG_TAG = "54BE6C87_AADA" // AADA = AiringAnimeDetailAdapter
        private const val fieldCount = 7
    }

    // Create a `View` from the `Layout`
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AiringAnimeDetailHolder {
        return AiringAnimeDetailHolder(
            ListItemAiringAnimeDetailBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AiringAnimeDetailHolder, position: Int) {
        val text = when (position) {
            0 -> "Season: ${anime.season}"
            1 -> "Original name: ${anime.originalName}"
            2 -> "Latest episode: ${anime.latestEpisode}"
            3 -> "Total episodes: ${anime.totalEpisodes}"
            4 -> "Release date: ${anime.releaseDate}"
            5 -> "Next episode date: ${anime.nextEpisodeDate?.getDisplayString() ?: "" }"
            6 -> "Minimum age: ${anime.minAge}"
            else -> ""
        }
        holder.bind(text)
    }

    // Tells the `RecyclerView` how many items to display
    override fun getItemCount(): Int {
        return fieldCount
    }
}
