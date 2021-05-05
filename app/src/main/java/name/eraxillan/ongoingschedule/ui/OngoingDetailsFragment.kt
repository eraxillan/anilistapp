package name.eraxillan.ongoingschedule.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import name.eraxillan.ongoingschedule.ui.adapter.OngoingItemsRecyclerViewAdapter
import name.eraxillan.ongoingschedule.databinding.FragmentOngoingDetailsBinding
import name.eraxillan.ongoingschedule.model.Ongoing

/**
 * A simple [Fragment] subclass.
 * Use the [OngoingDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OngoingDetailsFragment : Fragment() {
    private val LOG_TAG = OngoingDetailsFragment::class.java.simpleName

    private var _binding: FragmentOngoingDetailsBinding? = null
    // This property is only valid between `onCreateView` and `onDestroyView`
    private val binding get() = _binding!!

    lateinit var ongoing: Ongoing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            ongoing = it.getParcelable(OngoingListActivity.INTENT_ONGOING_KEY)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentOngoingDetailsBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.lstOngoingInfo.adapter = OngoingItemsRecyclerViewAdapter(ongoing)
        binding.lstOngoingInfo.layoutManager = LinearLayoutManager(context)
    }

    companion object {
        private const val ARG_ONGOING = "ongoing"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment OngoingDetailsFragment.
         */
        @JvmStatic
        fun newInstance(ongoing: Ongoing): OngoingDetailsFragment {
            val fragment = OngoingDetailsFragment()
            val arguments = Bundle()
            arguments.putParcelable(ARG_ONGOING, ongoing)
            fragment.arguments = arguments
            return fragment
        }
    }
}
