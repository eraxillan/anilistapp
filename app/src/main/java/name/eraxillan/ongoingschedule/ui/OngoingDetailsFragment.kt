package name.eraxillan.ongoingschedule.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import name.eraxillan.ongoingschedule.ui.adapter.OngoingItemsRecyclerViewAdapter
import name.eraxillan.ongoingschedule.databinding.FragmentOngoingDetailsBinding


class OngoingDetailsFragment : Fragment() {
    companion object {
        private val LOG_TAG = OngoingDetailsFragment::class.java.simpleName
    }

    private var _binding: FragmentOngoingDetailsBinding? = null
    // This property is only valid between `onCreateView` and `onDestroyView`
    private val binding get() = _binding!!

    private val args: OngoingDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOngoingDetailsBinding.inflate(inflater, container, false)
        context ?: return binding.root

        binding.ongoingInfoList.adapter = OngoingItemsRecyclerViewAdapter(args.ongoing!!)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
