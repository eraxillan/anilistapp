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

package name.eraxillan.anilistapp.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import name.eraxillan.anilistapp.R
import name.eraxillan.anilistapp.databinding.BackdropPanelBinding
import name.eraxillan.anilistapp.model.*
import name.eraxillan.anilistapp.repository.PreferenceRepository
import timber.log.Timber


class BackdropPanel : ConstraintLayout {
    private var _binding: BackdropPanelBinding? = null
    private val binding get() = _binding!!

    private var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null

    var scrollUpAction: (() -> Unit)? = null

    constructor(context: Context): super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        // The layout for this activity is a Data Binding layout so it needs
        // to be inflated using DataBindingUtil.
        _binding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.backdrop_panel,this, true
        )
        check(_binding != null)

        if (attrs != null) {
            val typedArray: TypedArray = context.obtainStyledAttributes(
                attrs, R.styleable.BackdropPanel
            )

            // ...

            typedArray.recycle()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun show(isVisible: Boolean) { binding.filterContainerLayout.isVisible = isVisible }

    fun setupListeners(
        onApplyListener: (filterOptions: MediaFilter, sortOption: MediaSort) -> Unit
    ) {
        binding.yearInput.isCompleteListener = ChippedEditText.Listener {
            Timber.d("yearInput data binding completed")

            loadSortOption()
            loadFilterOptions()
        }

        binding.applyFiltersButton.setOnClickListener {
            // Setup filter options from UI
            val filterOptions = getFilterOptions()

            // Setup sort option from UI
            val sortOption = getSortOption()

            // Save filter and sort options to Android Shared Preferences
            saveFilterOptions(filterOptions)
            saveSortOption(sortOption)

            onApplyListener(filterOptions, sortOption)
            openBottomSheet()
        }
    }

    fun closeBottomSheet() {
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun openBottomSheet() {
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED

        scrollUpAction?.invoke()
    }

    fun setBehavior(behavior: BottomSheetBehavior<View?>) {
        mBottomSheetBehavior = behavior
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun getFilterOptions(): MediaFilter {
        return MediaFilter(
            search = binding.searchInput.text.toString(),
            year = binding.yearInput.checkedElementAsInteger,
            season = binding.seasonInput.checkedElementAsEnumEntry<MediaSeason>(),
            formats = binding.formatInput.checkedElementAsEnumEntries(),
            status = binding.airingStatusInput.checkedElementAsEnumEntry<MediaStatus>(),
            country = binding.countryInput.checkedElementAsEnumEntry<MediaCountry>(),
            sources = binding.sourceInput.checkedElementAsEnumEntries(),
            isLicensed = (!binding.doujinInput.isChecked),
            genres = binding.genresInput.checkedElementAsStrings,
            tags = binding.tagsInput.checkedElementAsStrings,
            services = binding.streamingServicesInput.checkedElementAsStrings,
        )
    }

    private fun setFilterOptions(filterOptions: MediaFilter) {
        binding.apply {
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
        return binding.sortInput.checkedElementAsEnumEntry<MediaSort>()
            ?: MediaSort.BY_POPULARITY
    }

    private fun setSortOption(sortOption: MediaSort) {
        binding.sortInput.checkEnumerationElement(sortOption)
    }

    private fun loadFilterOptions() {
        val preferences = PreferenceRepository.getInstance(context)
        val filterOptions = preferences.filterOptions
        setFilterOptions(filterOptions)
    }

    private fun loadSortOption() {
        check(binding.sortInput.selectionMode == ChippedEditText.SINGLE_CHOICE)

        val preferences = PreferenceRepository.getInstance(context)
        setSortOption(preferences.sortOption)
    }

    private fun saveFilterOptions(filter: MediaFilter) {
        val preferences = PreferenceRepository.getInstance(context)
        preferences.filterOptions = filter
    }

    private fun saveSortOption(sort: MediaSort) {
        val preferences = PreferenceRepository.getInstance(context)
        preferences.sortOption = sort
    }
}
