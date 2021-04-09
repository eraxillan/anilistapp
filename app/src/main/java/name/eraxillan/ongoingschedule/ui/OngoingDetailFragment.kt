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
import name.eraxillan.ongoingschedule.TaskList

/**
 * A simple [Fragment] subclass.
 * Use the [OngoingDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OngoingDetailFragment : Fragment() {
    lateinit var listItemsRecyclerView: RecyclerView
    lateinit var list: TaskList

    fun addTask(item: String) {
        list.tasks.add(item)

        val listRecyclerAdapter = listItemsRecyclerView.adapter as OngoingItemsRecyclerViewAdapter
        listRecyclerAdapter.list = list
        listRecyclerAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            list = it.getParcelable(OngoingListActivity.INTENT_LIST_KEY)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_detail, container, false)

        view?.let {
            listItemsRecyclerView = it.findViewById(R.id.lst_ongoing_info)
            listItemsRecyclerView.adapter = OngoingItemsRecyclerViewAdapter(list)
            listItemsRecyclerView.layoutManager = LinearLayoutManager(context)
        }

        return view
    }

    companion object {
        private const val ARG_LIST = "list"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment OngoingDetailFragment.
         */
        @JvmStatic
        fun newInstance(list: TaskList): OngoingDetailFragment {
            val fragment = OngoingDetailFragment()
            val arguments = Bundle()
            arguments.putParcelable(ARG_LIST, list)
            fragment.arguments = arguments
            return fragment
        }
    }
}