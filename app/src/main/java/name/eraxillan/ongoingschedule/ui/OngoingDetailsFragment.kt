package name.eraxillan.ongoingschedule.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.ui.adapter.OngoingItemsRecyclerViewAdapter
import name.eraxillan.ongoingschedule.R
import name.eraxillan.ongoingschedule.model.Ongoing

/**
 * A simple [Fragment] subclass.
 * Use the [OngoingDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OngoingDetailsFragment : Fragment() {
    lateinit var listItemsRecyclerView: RecyclerView
    lateinit var ongoing: Ongoing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            ongoing = it.getParcelable(OngoingListActivity.INTENT_ONGOING_KEY)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ongoing_details, container, false)

        view?.let {
            listItemsRecyclerView = it.findViewById(R.id.lst_ongoing_info)
            listItemsRecyclerView.adapter = OngoingItemsRecyclerViewAdapter(ongoing)
            listItemsRecyclerView.layoutManager = LinearLayoutManager(context)
        }

        return view
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
