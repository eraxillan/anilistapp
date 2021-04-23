package name.eraxillan.ongoingschedule.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import name.eraxillan.ongoingschedule.ui.adapter.OngoingSelectionRecyclerViewAdapter
import name.eraxillan.ongoingschedule.R
import name.eraxillan.ongoingschedule.model.Ongoing
import name.eraxillan.ongoingschedule.viewmodel.OngoingViewModel
import java.net.URL

/**
 * A simple [Fragment] (isolated view) subclass.
 * Use the [OngoingSelectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OngoingSelectionFragment
    : Fragment()
    , OngoingSelectionRecyclerViewAdapter.OngoingSelectionRecyclerViewClickListener {

    private val TAG = OngoingSelectionFragment::class.java.simpleName

    private var listener: OnOngoingInfoFragmentInteractionListener? = null
    private lateinit var lstOngoings: RecyclerView

    /**
     * `by viewModels<MainViewModel>()` is a lazy delegate that creates a new `mainViewModel`
     * only the first time the `Activity` is created.
     * If a configuration change happens, such as a screen rotation,
     * it returns the previously created `MainViewModel`
     */
    private val ongoingViewModel by viewModels<OngoingViewModel>()

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun addOngoing(url: URL) {
        /*val job =*/ GlobalScope.launch(Dispatchers.IO) {
            // 1) Parse ongoing data from website
            val ongoing = ongoingViewModel.parseOngoingFromUrl(url)

            // 2) Save ongoing to database
            ongoingViewModel.addOngoing(ongoing)

            // 3) Add ongoing to list view in UI
            withContext(Dispatchers.Main) {
                val recyclerAdapter = lstOngoings.adapter as OngoingSelectionRecyclerViewAdapter
                recyclerAdapter.addOngoing(ongoing)
            }

            listener?.onOngoingAdded(ongoing)
        }
        //job.cancelAndJoin()
    }

    /*
    fun saveOngoing(ongoing: Ongoing) {
        listDataManager.saveList(list)

        updateOngoings()
    }
     */

    /*
    private fun updateOngoings() {
        //val lists = listDataManager.readLists()
        val ongoings: ArrayList<Ongoing> = ArrayList()
        lstOngoings.adapter = OngoingSelectionRecyclerViewAdapter(ongoings, this)
    }
    */

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Lifecycle method.
    // `onAttach` is run when the `Fragment` is first associated with an `Activity`,
    // giving a chance to set up anything required before `Fragment` is created
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnOngoingInfoFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnOngoingInfoFragmentInteractionListener")
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

        view.let {
            setupRecyclerView()
        }

        createOngoingObserver()
    }

    // Lifecycle method.
    // `onDetach` is run when the `Fragment` is no longer attached to an `Activity`.
    // This happens when the `Activity` containing the `Fragment` is destroyed,
    // or the `Fragment` is removed
    override fun onDetach() {
        super.onDetach()

        listener = null
    }

    // `OngoingSelectionRecyclerViewAdapter.OngoingSelectionRecyclerViewClickListener` implementation
    override fun ongoingClicked(ongoing: Ongoing) {
        listener?.onOngoingClicked(ongoing)
    }

    private fun setupRecyclerView() {
        val ongoings: ArrayList<Ongoing> = ArrayList()
        val adapter = OngoingSelectionRecyclerViewAdapter(ongoings, this)

        view?.let {
            lstOngoings = it.findViewById(R.id.lst_ongoings)

            lstOngoings.layoutManager = LinearLayoutManager(activity)
            lstOngoings.adapter = adapter

            val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            lstOngoings.addItemDecoration(divider)

            val itemTouchHelperCallback = ItemTouchHelperCallback(adapter)
            val touchHelper = ItemTouchHelper(itemTouchHelperCallback)
            touchHelper.attachToRecyclerView(lstOngoings)
        }
    }

    private fun createOngoingObserver() {
        ongoingViewModel.getOngoings()?.observe(
            viewLifecycleOwner, {
                // Clean old ongoing list
                val recyclerAdapter = lstOngoings.adapter as OngoingSelectionRecyclerViewAdapter
                recyclerAdapter.clearOngoings()

                // Add new ongoing list
                it?.let {
                    displayAllOngoings(it)
                }
            }
        )
    }

    private fun displayAllOngoings(ongoings: List<Ongoing>) {
        val recyclerAdapter = lstOngoings.adapter as OngoingSelectionRecyclerViewAdapter
        ongoings.forEach { recyclerAdapter.addOngoing(it) }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Inform objects that a list has been tapped.
    // `OngoingListActivity` will implement this interface
    interface OnOngoingInfoFragmentInteractionListener {
        fun onOngoingAdded(ongoing: Ongoing)
        fun onOngoingClicked(ongoing: Ongoing)
    }

    companion object {
        @JvmStatic
        fun newInstance(): OngoingSelectionFragment = OngoingSelectionFragment()
    }
}
