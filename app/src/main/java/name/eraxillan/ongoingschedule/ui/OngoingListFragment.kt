package name.eraxillan.ongoingschedule.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import name.eraxillan.ongoingschedule.R
import name.eraxillan.ongoingschedule.ui.adapter.OngoingSelectionRecyclerViewAdapter
import name.eraxillan.ongoingschedule.databinding.FragmentOngoingListBinding
import name.eraxillan.ongoingschedule.model.Ongoing
import name.eraxillan.ongoingschedule.viewmodel.OngoingViewModel
import java.net.MalformedURLException
import java.net.URL


class OngoingListFragment
    : Fragment()
    , OngoingSelectionRecyclerViewAdapter.OngoingSelectionRecyclerViewClickListener {

    private val LOG_TAG = OngoingListFragment::class.java.simpleName

    private var listener: OnOngoingInfoFragmentInteractionListener? = null

    private var _binding: FragmentOngoingListBinding? = null
    // This property is only valid between `onCreateView` and `onDestroyView`
    private val binding get() = _binding!!

    /**
     * `by viewModels<MainViewModel>()` is a lazy delegate that creates a new `mainViewModel`
     * only the first time the `Activity` is created.
     * If a configuration change happens, such as a screen rotation,
     * it returns the previously created `MainViewModel`
     */
    private val ongoingViewModel by viewModels<OngoingViewModel>()

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showAddOngoingDialog() {
        val dialogTitle = getString(R.string.add_ongoing_dialog_title)
        val positiveButtonTitle = getString(R.string.add_ongoing)

        val builder = AlertDialog.Builder(requireContext())
        val ongoingUrlEditText = EditText(requireContext())
        ongoingUrlEditText.hint = getString(R.string.add_ongoing_hint)
        ongoingUrlEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
        ongoingUrlEditText.setText(getString(R.string.invalid_url))

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!Patterns.WEB_URL.matcher(ongoingUrlEditText.text.toString()).matches())
                    ongoingUrlEditText.error = getString(R.string.invalid_ongoing_url_msg)
            }
        }
        ongoingUrlEditText.addTextChangedListener(textWatcher)

        builder.setTitle(dialogTitle)
        builder.setView(ongoingUrlEditText)

        builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
            val url: URL = try {
                URL(ongoingUrlEditText.text.toString())
            } catch (exc: MalformedURLException) {
                Toast.makeText(
                    requireContext(), getString(R.string.invalid_ongoing_url_msg), Toast.LENGTH_SHORT
                ).show()
                return@setPositiveButton
            }
            // Add ongoing to list view, db, and close dialog
            addOngoing(url)
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun addOngoing(url: URL) {
        /*val job =*/ GlobalScope.launch(Dispatchers.IO) {
            // Parse ongoing data from website
            val ongoing = ongoingViewModel.parseOngoingFromUrl(url)

            // Save ongoing to database
            ongoingViewModel.addOngoing(ongoing)

            withContext(Dispatchers.Main) {
                listener?.onOngoingAdded(ongoing)
            }
        }
        //job.cancelAndJoin()
    }

    // See https://developer.android.com/training/swipe/add-swipe-interface
    private fun updateOngoingList(fromMenu: Boolean) {
        // Signal SwipeRefreshLayout to start the progress indicator
        // NOTE: required only when called explicitly, e.g. from a menu item
        if (fromMenu)
            binding.swipeRefresh.isRefreshing = true

        GlobalScope.launch {
            // FIXME: implement ongoing list loading
            delay(1000)

            activity?.runOnUiThread {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentOngoingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    // NOTE: fragments outlive their views!
    //       One must clean up any references to the binging class instance here
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeOnRefresh()
        createOngoingObserver()

        binding.fabAddOngoing.setOnClickListener {
            showAddOngoingDialog()
        }
    }

    // Lifecycle method.
    // `onDetach` is run when the `Fragment` is no longer attached to an `Activity`.
    // This happens when the `Activity` containing the `Fragment` is destroyed,
    // or the `Fragment` is removed
    override fun onDetach() {
        super.onDetach()

        listener = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    // This hook is called whenever an item in your options menu is selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            // https://developer.android.com/training/swipe/respond-refresh-request
            R.id.action_refresh -> {
                Log.i(LOG_TAG, "Refresh menu item selected")

                // Start the refresh background task.
                // This method calls `setRefreshing(false)` when it's finished
                updateOngoingList(true)

                true
            }
            R.id.action_settings -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // `OngoingSelectionRecyclerViewAdapter.OngoingSelectionRecyclerViewClickListener` implementation
    override fun ongoingClicked(ongoing: Ongoing) {
        listener?.onOngoingClicked(ongoing)
    }

    private fun setupRecyclerView() {
        val ongoings = mutableListOf<Ongoing>()

        val adapter = OngoingSelectionRecyclerViewAdapter(ongoings, this) {
            GlobalScope.launch {
                ongoingViewModel.deleteOngoing(it)
            }
        }

        val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)

        val itemTouchHelperCallback = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(itemTouchHelperCallback)

        with (binding.lstOngoings) {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(requireContext())
            this.addItemDecoration(divider)
            this.setHasFixedSize(true)

            touchHelper.attachToRecyclerView(this)
        }
    }

    // https://developer.android.com/training/swipe/respond-refresh-request
    private fun setupSwipeOnRefresh() {
        with (binding.swipeRefresh) {
            // Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked
            // when the user performs a swipe-to-refresh gesture.
            this.setOnRefreshListener {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout")

                // This method performs the actual data-refresh operation.
                // The method calls `setRefreshing(false)` when it's finished
                updateOngoingList(false)
            }
        }
    }

    private fun createOngoingObserver() {
        ongoingViewModel.getOngoings()?.observe(
            viewLifecycleOwner, {
                // Clean old ongoing list
                getAdapter().clearOngoings()

                // Add new ongoing list
                it?.let {
                    displayAllOngoings(it)
                }
            }
        )
    }

    private fun displayAllOngoings(ongoings: List<Ongoing>) {
        ongoings.forEach { getAdapter().addOngoing(it) }
    }

    private fun getAdapter() = binding.lstOngoings.adapter as OngoingSelectionRecyclerViewAdapter
    
////////////////////////////////////////////////////////////////////////////////////////////////

    // Inform objects that a list has been tapped.
    // `OngoingListActivity` will implement this interface
    interface OnOngoingInfoFragmentInteractionListener {
        fun onOngoingAdded(ongoing: Ongoing)
        fun onOngoingClicked(ongoing: Ongoing)
    }

    companion object {
        @JvmStatic
        fun newInstance(): OngoingListFragment = OngoingListFragment()
    }
}
