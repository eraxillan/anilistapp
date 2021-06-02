package name.eraxillan.airinganimeschedule.ui

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
import com.google.gson.*
import kotlinx.coroutines.*
import name.eraxillan.airinganimeschedule.R
import name.eraxillan.airinganimeschedule.ui.adapter.AiringAnimeListAdapter
import name.eraxillan.airinganimeschedule.databinding.FragmentAiringAnimeListBinding
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.viewmodel.AiringAnimeViewModel
import java.net.MalformedURLException
import java.net.URL
import java.time.*


class AiringAnimeListFragment : Fragment() {
    companion object {
        private const val LOG_TAG = "54BE6C87_AALF" // AALF = AiringAnimeListFragment
    }

    private var _binding: FragmentAiringAnimeListBinding? = null
    // This property is only valid between `onCreateView` and `onDestroyView`
    private val binding get() = _binding!!

    /**
     * `by viewModels<MainViewModel>()` is a lazy delegate that creates a new `mainViewModel`
     * only the first time the `Activity` is created.
     * If a configuration change happens, such as a screen rotation,
     * it returns the previously created `MainViewModel`
     */
    private val viewModel by viewModels<AiringAnimeViewModel>()

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showAddAiringAnimeDialog() {
        val dialogTitle = getString(R.string.add_airing_anime_dialog_title)
        val positiveButtonTitle = getString(R.string.add_airing_anime_button_hint)

        val builder = AlertDialog.Builder(requireContext())
        val airingAnimeUrlEditText = EditText(requireContext())
        airingAnimeUrlEditText.hint = getString(R.string.add_airing_anime_hint)
        airingAnimeUrlEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
        airingAnimeUrlEditText.setText(getString(R.string.invalid_url))

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!Patterns.WEB_URL.matcher(airingAnimeUrlEditText.text.toString()).matches())
                    airingAnimeUrlEditText.error = getString(R.string.invalid_airing_anime_url_msg)
            }
        }
        airingAnimeUrlEditText.addTextChangedListener(textWatcher)

        builder.setTitle(dialogTitle)
        builder.setView(airingAnimeUrlEditText)

        builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
            val url: URL = try {
                URL(airingAnimeUrlEditText.text.toString())
            } catch (exc: MalformedURLException) {
                Toast.makeText(
                    requireContext(), getString(R.string.invalid_airing_anime_url_msg), Toast.LENGTH_SHORT
                ).show()
                return@setPositiveButton
            }
            // Add airing anime to list view, db, and close dialog
            addAiringAnime(url)
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun addAiringAnime(url: URL) {
        viewModel.addAiringAnime(url, findNavController())
    }

    // See https://developer.android.com/training/swipe/add-swipe-interface
    private fun updateAiringAnimeList(fromMenu: Boolean) {
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
        _binding = FragmentAiringAnimeListBinding.inflate(inflater, container, false)
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
        createAiringAnimeObserver()

        binding.addAiringAnimeFab.setOnClickListener {
            showAddAiringAnimeDialog()
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
                updateAiringAnimeList(true)

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
        val adapter = AiringAnimeListAdapter() {
            viewModel.deleteAiringAnime(it)
        }

        val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)

        val itemTouchHelperCallback = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(itemTouchHelperCallback)

        with (binding.airingAnimeList) {
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
                updateAiringAnimeList(false)
            }
        }
    }

    private fun createAiringAnimeObserver() {
        viewModel.getAiringAnimeList()?.observe(
            viewLifecycleOwner, {
                // Clean old anime list
                getAdapter().clearAiringAnimeList()

                // Add new anime list
                displayAiringAnimeList(it)
            }
        )
    }

    private fun displayAiringAnimeList(anime: List<AiringAnime>) {
        anime.forEach { getAdapter().addAiringAnime(it) }
    }

    private fun getAdapter() = binding.airingAnimeList.adapter as AiringAnimeListAdapter
}
