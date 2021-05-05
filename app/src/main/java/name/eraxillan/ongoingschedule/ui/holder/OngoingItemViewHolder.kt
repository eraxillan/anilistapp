package name.eraxillan.ongoingschedule.ui.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.databinding.ViewHolderOngoingDetailsBinding

class OngoingItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = ViewHolderOngoingDetailsBinding.bind(itemView)

    companion object {
        private val TAG = OngoingItemViewHolder::class.java.simpleName
    }

    fun bind(text: String) {
        with (binding) {
            tvOngoingInfo.text = text
        }
    }
}
