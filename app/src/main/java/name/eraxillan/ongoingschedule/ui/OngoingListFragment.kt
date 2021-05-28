package name.eraxillan.ongoingschedule.ui

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.coroutines.*
import name.eraxillan.ongoingschedule.R
import name.eraxillan.ongoingschedule.ui.adapter.OngoingSelectionRecyclerViewAdapter
import name.eraxillan.ongoingschedule.databinding.FragmentOngoingListBinding
import name.eraxillan.ongoingschedule.model.AiringAnime
import name.eraxillan.ongoingschedule.viewmodel.OngoingViewModel
import java.net.MalformedURLException
import java.net.URL


class OngoingListFragment : Fragment() {
    companion object {
        private val LOG_TAG = OngoingListFragment::class.java.simpleName
    }

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
        val positiveButtonTitle = getString(R.string.add_ongoing_button_hint)

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
            // Add airing anime to list view, db, and close dialog
            addOngoing(url)
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun addOngoing(url: URL) {
        /*val job =*/ GlobalScope.launch(Dispatchers.IO) {
            // Parse airing anime data from website
            val anime = ongoingViewModel.parseOngoingFromUrl(url)

            // Save airing anime to database
            ongoingViewModel.addOngoing(anime)

            withContext(Dispatchers.Main) {
                showOngoingInfo(anime, findNavController())
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
            // FIXME: implement airing anime list loading
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

        binding.addOngoingFab.setOnClickListener {
            showAddOngoingDialog()
        }
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

    private fun setupRecyclerView() {
        val adapter = OngoingSelectionRecyclerViewAdapter() {
            GlobalScope.launch {
                ongoingViewModel.deleteOngoing(it)
            }
        }

        val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)

        val itemTouchHelperCallback = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(itemTouchHelperCallback)

        with (binding.ongoingList) {
            this.adapter = adapter
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
                // Clean old anime list
                getAdapter().clearOngoings()

                // Add new anime list
                displayAllOngoings(it)

            }
        )
    }

    private fun displayAllOngoings(anime: List<AiringAnime>) {
        anime.forEach { getAdapter().addOngoing(it) }
    }

    private fun getAdapter() = binding.ongoingList.adapter as OngoingSelectionRecyclerViewAdapter
}
