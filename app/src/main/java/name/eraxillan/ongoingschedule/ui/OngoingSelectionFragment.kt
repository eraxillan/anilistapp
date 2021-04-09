package name.eraxillan.ongoingschedule.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.ongoingschedule.ListDataManager
import name.eraxillan.ongoingschedule.OngoingSelectionRecyclerViewAdapter
import name.eraxillan.ongoingschedule.R
import name.eraxillan.ongoingschedule.TaskList

/**
 * A simple [Fragment] (isolated view) subclass.
 * Use the [OngoingSelectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OngoingSelectionFragment
    : Fragment()
    , OngoingSelectionRecyclerViewAdapter.ListSelectionRecyclerViewClickListener {

    private var listener: OnListItemFragmentInteractionListener? = null
    private lateinit var lstOngoings: RecyclerView
    lateinit var listDataManager: ListDataManager

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun addList(list: TaskList) {
        listDataManager.saveList(list)

        val recyclerAdapter = lstOngoings.adapter as OngoingSelectionRecyclerViewAdapter
        recyclerAdapter.addList(list)
    }

    fun saveList(list: TaskList) {
        listDataManager.saveList(list)
        updateLists()
    }

    private fun updateLists() {
        val lists = listDataManager.readLists()
        lstOngoings.adapter = OngoingSelectionRecyclerViewAdapter(lists, this)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Lifecycle method.
    // `onAttach` is run when the `Fragment` is first associated with an `Activity`,
    // giving a chance to set up anything required before `Fragment` is created
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnListItemFragmentInteractionListener) {
            listener = context
            listDataManager = ListDataManager(context)
        } else {
            throw RuntimeException("$context must implement OnListItemFragmentInteractionListener")
        }
    }

    // Lifecycle method.
    // `onCreate` is run when the `Fragment` is in the process of being created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_selection, container, false)
    }

    // Lifecycle method.
    // `onActivityCreated` is the when the `Activity` to which the `Fragment` is attached
    // has finished running its lifecycle method `onCreate`.
    // This ensures you have an `Activity` to work with and something to show your widgets
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lists = listDataManager.readLists()
        view.let {
            lstOngoings = it.findViewById(R.id.lst_ongoings)
            lstOngoings.layoutManager = LinearLayoutManager(activity)
            lstOngoings.adapter = OngoingSelectionRecyclerViewAdapter(lists, this)
        }

        // FIXME: implement Edit/Delete buttons first
        //setRecyclerViewItemTouchListener()
    }

    // Lifecycle method.
    // `onDetach` is run when the `Fragment` is no longer attached to an `Activity`.
    // This happens when the `Activity` containing the `Fragment` is destroyed,
    // or the `Fragment` is removed
    override fun onDetach() {
        super.onDetach()

        listener = null
    }

    // `OngoingSelectionRecyclerViewAdapter.ListSelectionRecyclerViewClickListener` implementation
    override fun listItemClicked(list: TaskList) {
        listener?.onListItemClicked(list)
    }

    private fun setRecyclerViewItemTouchListener() {

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.adapterPosition
                //photosList.removeAt(position)
                //recyclerView.adapter!!.notifyItemRemoved(position)

                // FIXME: implement
                val recyclerAdapter = lstOngoings.adapter as OngoingSelectionRecyclerViewAdapter
                //recyclerAdapter.removeList(list)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(lstOngoings)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Inform objects that a list has been tapped.
    // `OngoingListActivity` will implement this interface
    interface OnListItemFragmentInteractionListener {
        fun onListItemClicked(list: TaskList)
    }

    companion object {
        fun newInstance(): OngoingSelectionFragment = OngoingSelectionFragment()

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OngoingSelectionFragment.
         */
        // TODO: Rename and change types and number of parameters
        /*
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                OngoingSelectionFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
         */
    }
}
