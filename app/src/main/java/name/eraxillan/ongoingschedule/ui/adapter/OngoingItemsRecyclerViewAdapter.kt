package name.eraxillan.ongoingschedule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.databinding.ListItemOngoingDetailsBinding
import name.eraxillan.ongoingschedule.model.AiringAnime
import name.eraxillan.ongoingschedule.ui.holder.OngoingItemViewHolder

class OngoingItemsRecyclerViewAdapter(
    var anime: AiringAnime
)
    : RecyclerView.Adapter<OngoingItemViewHolder>() {

    companion object {
        private val LOG_TAG = OngoingItemsRecyclerViewAdapter::class.java.simpleName
        private const val fieldCount = 7
    }

    // Create a `View` from the `Layout`
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingItemViewHolder {
        return OngoingItemViewHolder(
            ListItemOngoingDetailsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: OngoingItemViewHolder, position: Int) {
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
