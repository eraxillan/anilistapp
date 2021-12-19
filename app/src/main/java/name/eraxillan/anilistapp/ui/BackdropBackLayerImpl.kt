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

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import name.eraxillan.anilistapp.R
import name.eraxillan.anilistapp.databinding.BackdropBackLayerImplBinding
import name.eraxillan.anilistapp.model.*
import name.eraxillan.anilistapp.repository.PreferenceRepository
import name.eraxillan.customviews.ChippedEditText
import timber.log.Timber


class BackdropBackLayerImpl: ConstraintLayout {
    private var _binding: BackdropBackLayerImplBinding? = null
    private val binding get() = _binding!!

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
            LayoutInflater.from(context), R.layout.backdrop_back_layer_impl,this, true
        )
        check(_binding != null)

        if (attrs != null) {
            val typedArray: TypedArray = context.obtainStyledAttributes(
                attrs, R.styleable.BackdropBackLayerImpl
            )

            // ...

            typedArray.recycle()
        }
    }

    /*override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        updateLayoutParams<MarginLayoutParams> {
            this.setMargins(0, actionBarHeight(context), 0, 0)
        }
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun setupListeners(
        onApplyListener: (filterOptions: MediaFilter, sortOption: MediaSort) -> Unit
    ) {
        binding.yearInput.onCompleteListener = {
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
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun getFilterOptions(): MediaFilter {
        return MediaFilter(
            search = binding.searchInput.text.toString(),
            year = binding.yearInput.checkedElementValue(),
            season = binding.seasonInput.checkedElementValue<MediaSeason>(),
            formats = binding.formatInput.checkedElementsValues(),
            status = binding.airingStatusInput.checkedElementValue<MediaStatus>(),
            country = binding.countryInput.checkedElementValue<MediaCountry>(),
            sources = binding.sourceInput.checkedElementsValues(),
            isLicensed = (!binding.doujinInput.isChecked),
            genres = binding.genresInput.checkedElementsValues(),
            tags = binding.tagsInput.checkedElementsValues(),
            services = binding.streamingServicesInput.checkedElementsValues(),
        )
    }

    private fun setFilterOptions(filterOptions: MediaFilter) {
        binding.apply {
            searchInput.setText(filterOptions.search)
            yearInput.checkElement(filterOptions.year)
            seasonInput.checkElement(filterOptions.season)
            formatInput.checkElements(filterOptions.formats)
            airingStatusInput.checkElement(filterOptions.status)
            countryInput.checkElement(filterOptions.country)
            sourceInput.checkElements(filterOptions.sources)
            doujinInput.isChecked = filterOptions.isLicensed == false
            genresInput.checkElements(filterOptions.genres)
            tagsInput.checkElements(filterOptions.tags)
            streamingServicesInput.checkElements(filterOptions.services)
        }
    }

    private fun getSortOption(): MediaSort {
        return binding.sortInput.checkedElementValue<MediaSort>()
            ?: MediaSort.BY_POPULARITY
    }

    private fun setSortOption(sortOption: MediaSort) {
        binding.sortInput.checkElement(sortOption)
    }

    private fun loadFilterOptions() {
        val preferences = PreferenceRepository.getInstance(context)
        val filterOptions = preferences.filterOptions
        setFilterOptions(filterOptions)
    }

    private fun loadSortOption() {
        check(binding.sortInput.selectionMode == ChippedEditText.SINGLE_CHOICE_SELECTION)

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
