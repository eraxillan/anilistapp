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
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import name.eraxillan.airinganimeschedule.R
import name.eraxillan.airinganimeschedule.ui.adapter.AiringAnimeListAdapter
import name.eraxillan.airinganimeschedule.databinding.FragmentAiringAnimeListBinding
import name.eraxillan.airinganimeschedule.ui.adapter.AnimeListLoadStateAdapter
import name.eraxillan.airinganimeschedule.viewmodel.AiringAnimeViewModel


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
     * it returns the previously created `AiringAnimeViewModel`
     */
    private val viewModel by viewModels<AiringAnimeViewModel>()

    private lateinit var listAdapter: AiringAnimeListAdapter

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // See https://developer.android.com/training/swipe/add-swipe-interface
    private fun updateAiringAnimeList(fromMenu: Boolean) {
        // Signal SwipeRefreshLayout to start the progress indicator
        // NOTE: required only when called explicitly, e.g. from a menu item
        if (fromMenu)
            binding.swipeRefresh.isRefreshing = true

        lifecycleScope.launch {
            // FIXME: implement airing anime list force loading from Anilist/Database
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
        listAdapter = AiringAnimeListAdapter()
        val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)

        with (binding.airingAnimeList) {
            this.adapter = listAdapter.withLoadStateHeaderAndFooter(
                header = AnimeListLoadStateAdapter(listAdapter),
                footer = AnimeListLoadStateAdapter(listAdapter)
            )
            this.addItemDecoration(divider)
            this.setHasFixedSize(true)
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
        // Observe airing anime list loading
        viewModel.getRemoteAiringAnimeList()?.observe(
            viewLifecycleOwner, { animeList ->
                binding.swipeRefresh.isRefreshing = true

                /*val job = */ lifecycleScope.launch {
                    listAdapter.submitData(animeList)
                }

                binding.swipeRefresh.isRefreshing = false
            }
        )

        // Observe airing anime list loading states
        viewLifecycleOwner.lifecycleScope.launch {
            listAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }
    }
}
