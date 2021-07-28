/*
 * Copyright 2021 Aleksandr Kamyshnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package name.eraxillan.airinganimeschedule.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import name.eraxillan.airinganimeschedule.R
import name.eraxillan.airinganimeschedule.ui.adapter.MediaListAdapter
import name.eraxillan.airinganimeschedule.databinding.FragmentMediaListBinding
import name.eraxillan.airinganimeschedule.ui.adapter.MediaListLoadStateAdapter
import name.eraxillan.airinganimeschedule.viewmodel.MediaViewModel
import timber.log.Timber


class MediaListFragment : Fragment() {
    private var _binding: FragmentMediaListBinding? = null
    // This property is only valid between `onCreateView` and `onDestroyView`
    private val binding get() = _binding!!

    /**
     * `by viewModels<MediaViewModel>()` is a lazy delegate that creates a new `viewModel`
     * only the first time the `Activity` is created.
     * If a configuration change happens, such as a screen rotation,
     * it returns the previously created `MediaViewModel`
     */
    private val viewModel by viewModels<MediaViewModel>()

    private var searchJob: Job? = null
    private val listAdapter: MediaListAdapter = MediaListAdapter()

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // See https://developer.android.com/training/swipe/add-swipe-interface
    private fun updateMediaList(fromMenu: Boolean) {
        // Signal SwipeRefreshLayout to start the progress indicator
        // NOTE: required only when called explicitly, e.g. from a menu item
        if (fromMenu)
            binding.swipeRefresh.isRefreshing = true

        viewLifecycleOwner.lifecycleScope.launch {
            listAdapter.refresh()

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
        _binding = FragmentMediaListBinding.inflate(inflater, container, false)
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

        search()
        initSearch()

        binding.retryButton.setOnClickListener { listAdapter.retry() }
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
                Timber.i("Refresh menu item selected")

                // Start the refresh background task.
                // This method calls `setRefreshing(false)` when it's finished
                updateMediaList(true)

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
        // Add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)

        val header = MediaListLoadStateAdapter(listAdapter)

        with (binding.list) {
            this.adapter = listAdapter.withLoadStateHeaderAndFooter(
                header = header,
                footer = MediaListLoadStateAdapter(listAdapter)
            )
            this.addItemDecoration(decoration)
        }

        listAdapter.addLoadStateListener { loadState ->
            // Show empty list
            val isListEmpty = loadState.refresh is LoadState.NotLoading && listAdapter.itemCount == 0
            showEmptyList(isListEmpty)

            // Show a retry header if there was an error refreshing, and items were previously
            // cached OR default to the default prepend state
            header.loadState = loadState.mediator
                ?.refresh
                ?.takeIf { it is LoadState.Error && listAdapter.itemCount > 0 }
                ?: loadState.prepend

            // Only show the list if refresh succeeds, either from the the local db or the remote
            binding.list.isVisible = loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading

            // Show loading spinner during initial load or refresh
            binding.progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
            /*binding.swipeRefresh.isRefreshing = loadState.mediator?.refresh is LoadState.Loading*/

            // Show the retry state if initial load or refresh fails and there are no items
            binding.retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error && listAdapter.itemCount == 0

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    requireContext(),
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initSearch() {
        // NOTE: this behaviour looks like unnecessary
        // Scroll to top when the list is refreshed from network
        /*viewLifecycleOwner.lifecycleScope.launch {
            listAdapter.loadStateFlow
                // Only emit when REFRESH LoadState for RemoteMediator changes
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where Remote `REFRESH` completes i.e., `NotLoading`
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.list.scrollToPosition(0) }
        }*/
    }

    // https://developer.android.com/training/swipe/respond-refresh-request
    private fun setupSwipeOnRefresh() {
        with (binding.swipeRefresh) {
            // Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked
            // when the user performs a swipe-to-refresh gesture.
            this.setOnRefreshListener {
                Timber.i("onRefresh called from SwipeRefreshLayout")

                // This method performs the actual data-refresh operation.
                // The method calls `setRefreshing(false)` when it's finished
                updateMediaList(false)
            }
        }
    }

    private fun search() {
        // Make sure we cancel the previous job before creating a new one
        searchJob?.cancel()

        // Observe media list loading
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getMediaListStream().collectLatest {
                listAdapter.submitData(it)
            }
        }
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.emptyList.visibility = View.VISIBLE
            binding.list.visibility = View.GONE
        } else {
            binding.emptyList.visibility = View.GONE
            binding.list.visibility = View.VISIBLE
        }
    }
}
