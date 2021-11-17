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

package name.eraxillan.anilistapp.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import name.eraxillan.anilistapp.R
import name.eraxillan.anilistapp.databinding.ActivityMainBinding
import name.eraxillan.anilistapp.model.*
import name.eraxillan.anilistapp.repository.PreferenceRepository
import name.eraxillan.anilistapp.ui.views.ChippedEditText
import timber.log.Timber

/**
 * An interface to communicate between fragment and activity
 */
interface OnBottomSheetCallbacks {
    fun onStateChanged(bottomSheet: View, newState: Int)
}

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    // This property is only valid between `onCreateView` and `onDestroyView`
    private val binding get() = _binding!!

    private lateinit var appBarConfiguration: AppBarConfiguration

    private var listener: OnBottomSheetCallbacks? = null
    private var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        // NOTE: return to basic app theme from splash screen one;
        //       see https://medium.com/android-news/launch-screen-in-android-the-right-way-aca7e8c31f52
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar(binding)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

        configureBackdrop()
    }

    override fun onSupportNavigateUp(): Boolean {
        val result = findNavController().navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        binding.backdropViews.filterContainerLayout.isVisible = true
        return result
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setupActionBar(binding: ActivityMainBinding) {
        // NOTE: see https://developer.android.com/guide/navigation/navigation-ui#action_bar

        setSupportActionBar(binding.toolbarMain)

        appBarConfiguration = AppBarConfiguration(findNavController().graph, binding.drawerLayout)
        setupActionBarWithNavController(findNavController(), appBarConfiguration)
        binding.navView.setupWithNavController(findNavController())

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            // Show backdrop controls only on `MediaListFragment` ("Discover" menu item)
            binding.backdropViews.filterContainerLayout.isVisible = (menuItem.itemId == 2131296544)

            val isHandled = NavigationUI.onNavDestinationSelected(menuItem, findNavController())
            if (isHandled) {
                val parent = binding.navView.parent
                if (parent is DrawerLayout)
                    parent.closeDrawer(GravityCompat.START)
            }

            // Avoid layout shift to bottom 
            openBottomSheet()

            isHandled
        }

        // Set the elevation equal to zero to remove any shadows between the action bar
        // (same thing for the toolbar) and the layout
        supportActionBar?.elevation = 0f
    }

    private fun findNavController(): NavController {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host) as NavHostFragment
        return navHostFragment.navController
    }

    fun setOnBottomSheetCallbacks(onBottomSheetCallbacks: OnBottomSheetCallbacks) {
        this.listener = onBottomSheetCallbacks
    }

    fun closeBottomSheet() {
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun openBottomSheet() {
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED

        getMediaListFragment().scrollUp()
    }

    private fun configureBackdrop() {
        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host)

        (fragment?.view?.parent as View).let { view ->
            BottomSheetBehavior.from(view).let { bs ->

                bs.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        // Call the interface to notify a state change
                        listener?.onStateChanged(bottomSheet, newState)
                    }
                })

                // Set the bottom sheet expanded by default
                bs.state = BottomSheetBehavior.STATE_EXPANDED

                mBottomSheetBehavior = bs
            }
        }

        // Show backdrop controls only on `MediaListFragment` ("Discover" menu item)
        val navHost = supportFragmentManager.primaryNavigationFragment // NavHostFragment
        val currentFragment = navHost?.childFragmentManager?.primaryNavigationFragment
        binding.backdropViews.filterContainerLayout.isVisible = navHost == null || currentFragment is MediaListFragment

        binding.backdropViews.yearInput.isCompleteListener = ChippedEditText.Listener {
            Timber.d("yearInput data binding completed")
            loadSortOption()
            loadFilterOptions()
        }

        binding.backdropViews.applyFiltersButton.setOnClickListener {
            // Setup filter options from UI
            val filterOptions = getFilterOptions()

            // Setup sort option from UI
            val sortOption = getSortOption()

            // Save filter and sort options to Android Shared Preferences
            saveFilterOptions(filterOptions)
            saveSortOption(sortOption)

            Timber.d("Fetching filtered and sorted media list...")
            getMediaListFragment().search(filterOptions, sortOption)
            openBottomSheet()
        }
    }

    private fun getFilterOptions(): MediaFilter {
        return MediaFilter(
            search = binding.backdropViews.searchInput.text.toString(),
            year = binding.backdropViews.yearInput.checkedElementAsInteger,
            season = binding.backdropViews.seasonInput.checkedElementAsEnumEntry<MediaSeason>(),
            formats = binding.backdropViews.formatInput.checkedElementAsEnumEntries(),
            status = binding.backdropViews.airingStatusInput.checkedElementAsEnumEntry<MediaStatus>(),
            country = binding.backdropViews.countryInput.checkedElementAsEnumEntry<MediaCountry>(),
            sources = binding.backdropViews.sourceInput.checkedElementAsEnumEntries(),
            isLicensed = (!binding.backdropViews.doujinInput.isChecked),
            genres = binding.backdropViews.genresInput.checkedElementAsStrings,
            tags = binding.backdropViews.tagsInput.checkedElementAsStrings,
            services = binding.backdropViews.streamingServicesInput.checkedElementAsStrings,
        )
    }

    private fun setFilterOptions(filterOptions: MediaFilter) {
        binding.backdropViews.apply {
            searchInput.setText(filterOptions.search)
            yearInput.checkIntegerElement(filterOptions.year)
            seasonInput.checkEnumerationElement(filterOptions.season)
            formatInput.checkEnumerationElements(filterOptions.formats)
            airingStatusInput.checkEnumerationElement(filterOptions.status)
            countryInput.checkEnumerationElement(filterOptions.country)
            sourceInput.checkEnumerationElements(filterOptions.sources)
            doujinInput.isChecked = filterOptions.isLicensed == false
            genresInput.checkStringElements(filterOptions.genres)
            tagsInput.checkStringElements(filterOptions.tags)
            streamingServicesInput.checkStringElements(filterOptions.services)
        }
    }

    private fun getSortOption(): MediaSort {
        return binding.backdropViews.sortInput.checkedElementAsEnumEntry<MediaSort>()
            ?: MediaSort.BY_POPULARITY
    }

    private fun setSortOption(sortOption: MediaSort) {
        binding.backdropViews.sortInput.checkEnumerationElement(sortOption)
    }

    private fun loadFilterOptions() {
        val preferences = PreferenceRepository.getInstance(this)
        val filterOptions = preferences.filterOptions
        setFilterOptions(filterOptions)
    }

    private fun loadSortOption() {
        check(binding.backdropViews.sortInput.selectionMode == ChippedEditText.SINGLE_CHOICE)

        val preferences = PreferenceRepository.getInstance(this)
        setSortOption(preferences.sortOption)
    }

    private fun saveFilterOptions(filter: MediaFilter) {
        val preferences = PreferenceRepository.getInstance(this)
        preferences.filterOptions = filter
    }

    private fun saveSortOption(sort: MediaSort) {
        val preferences = PreferenceRepository.getInstance(this)
        preferences.sortOption = sort
    }

    private fun getMediaListFragment(): MediaListFragment {
        val navHost = supportFragmentManager.primaryNavigationFragment // NavHostFragment
        val currentFragment = navHost?.childFragmentManager?.primaryNavigationFragment
        return currentFragment as MediaListFragment
    }
}
