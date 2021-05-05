package name.eraxillan.ongoingschedule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.R
import name.eraxillan.ongoingschedule.model.Ongoing
import name.eraxillan.ongoingschedule.ui.holder.OngoingItemViewHolder

class OngoingItemsRecyclerViewAdapter(var ongoing: Ongoing)
    : RecyclerView.Adapter<OngoingItemViewHolder>() {

    private val TAG = OngoingItemsRecyclerViewAdapter::class.java.simpleName

    // Create a `View` from the `Layout`
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.view_holder_ongoing_details,
                parent,
                false
            )
        return OngoingItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OngoingItemViewHolder, position: Int) {
        val text = when (position) {
            0 -> "Season: ${ongoing.season}"
            1 -> "Original name: ${ongoing.originalName}"
            2 -> "Latest episode: ${ongoing.latestEpisode}"
            3 -> "Total episodes: ${ongoing.totalEpisodes}"
            4 -> "Release date: ${ongoing.releaseDate}"
            5 -> "Next episode date: ${ongoing.nextEpisodeDate?.getDisplayString() ?: "" }"
            6 -> "Minimum age: ${ongoing.minAge}"
            else -> ""
        }
        holder.bind(text)
    }

    // Tells the `RecyclerView` how many items to display
    override fun getItemCount(): Int {
        return 7
    }
}
