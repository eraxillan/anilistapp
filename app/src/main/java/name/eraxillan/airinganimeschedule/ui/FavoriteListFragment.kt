package name.eraxillan.airinganimeschedule.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import name.eraxillan.airinganimeschedule.R
import name.eraxillan.airinganimeschedule.databinding.FragmentFavoriteListBinding
import name.eraxillan.airinganimeschedule.ui.adapter.FavoriteListAdapter
import name.eraxillan.airinganimeschedule.viewmodel.AiringAnimeViewModel
import java.net.URL


class FavoriteListFragment : Fragment() {
    companion object {
        private const val LOG_TAG = "54BE6C87_FLF" // FLF = FavoriteListFragment
    }

    private var _binding: FragmentFavoriteListBinding? = null
    // This property is only valid between `onCreateView` and `onDestroyView`
    private val binding get() = _binding!!

    /**
     * `by viewModels<MainViewModel>()` is a lazy delegate that creates a new `mainViewModel`
     * only the first time the `Activity` is created.
     * If a configuration change happens, such as a screen rotation,
     * it returns the previously created `AiringAnimeViewModel`
     */
    private val viewModel by viewModels<AiringAnimeViewModel>()

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // See https://developer.android.com/training/swipe/add-swipe-interface
    private fun updateAiringAnimeList(fromMenu: Boolean) {
        // Signal SwipeRefreshLayout to start the progress indicator
        // NOTE: required only when called explicitly, e.g. from a menu item
        if (fromMenu)
            binding.favoriteSwipeRefresh.isRefreshing = true

        lifecycleScope.launch {
            // FIXME: implement airing anime list force loading from Anilist/Database
            delay(1000)

            activity?.runOnUiThread {
                binding.favoriteSwipeRefresh.isRefreshing = false
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
        _binding = FragmentFavoriteListBinding.inflate(inflater, container, false)
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
        val adapter = FavoriteListAdapter {
            viewModel.deleteAiringAnime(it)
        }

        val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)

        val itemTouchHelperCallback = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(itemTouchHelperCallback)

        with (binding.favoriteAnimeList) {
            this.adapter = adapter
            this.addItemDecoration(divider)
            this.setHasFixedSize(true)

            touchHelper.attachToRecyclerView(this)
        }
    }

    // https://developer.android.com/training/swipe/respond-refresh-request
    private fun setupSwipeOnRefresh() {
        with (binding.favoriteSwipeRefresh) {
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
            viewLifecycleOwner, { animeList ->
                // FIXME: add this call to beginning of db/anilist load function
                binding.favoriteSwipeRefresh.isRefreshing = true

                // Clean old anime list
                getAdapter().clearAiringAnimeList()

                // Add new anime list
                animeList.forEach { anime -> getAdapter().addAiringAnime(anime) }

                binding.favoriteSwipeRefresh.isRefreshing = false
            }
        )
    }

    private fun getAdapter() = binding.favoriteAnimeList.adapter as FavoriteListAdapter
}
