package name.eraxillan.ongoingschedule.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import name.eraxillan.ongoingschedule.ui.adapter.OngoingItemsRecyclerViewAdapter
import name.eraxillan.ongoingschedule.databinding.FragmentAnimeDetailBinding


class AnimeDetailFragment : Fragment() {
    companion object {
        private val LOG_TAG = AnimeDetailFragment::class.java.simpleName
    }

    private var _binding: FragmentAnimeDetailBinding? = null
    // This property is only valid between `onCreateView` and `onDestroyView`
    private val binding get() = _binding!!

    private val args: AnimeDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAnimeDetailBinding.inflate(inflater, container, false)
        context ?: return binding.root

        binding.animeFieldList.adapter = OngoingItemsRecyclerViewAdapter(args.anime!!)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
