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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import name.eraxillan.anilistapp.R
import name.eraxillan.anilistapp.databinding.ActivityMainBinding
import name.eraxillan.anilistapp.model.MediaSort
import timber.log.Timber


/**
 * An interface to communicate between fragment and activity
 */
interface OnBottomSheetCallbacks {
    fun onStateChanged(bottomSheet: View, newState: Int)
}

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOG_TAG = "54BE6C87_MA" // MainActivity
    }

    private var _binding: ActivityMainBinding? = null
    // This property is only valid between `onCreateView` and `onDestroyView`
    private val binding get() = _binding!!

    private lateinit var appBarConfiguration: AppBarConfiguration

    private var listener: OnBottomSheetCallbacks? = null
    private var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Perform initialization of all fragments
    override fun onCreate(savedInstanceState: Bundle?) {
        // NOTE: return to basic app theme from splash screen one;
        //       see https://medium.com/android-news/launch-screen-in-android-the-right-way-aca7e8c31f52
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar(binding)
        setSortGroupButtons()
    }

    // NOTE: fragments outlive their views!
    //       One must clean up any references to the binging class instance here
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

        configureBackdrop()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setupActionBar(binding: ActivityMainBinding) {
        // NOTE: see https://developer.android.com/guide/navigation/navigation-ui#action_bar

        /* setSupportActionBar(binding.toolbarMain) */

        appBarConfiguration = AppBarConfiguration(findNavController().graph, binding.drawerLayout)
        setupActionBarWithNavController(findNavController(), appBarConfiguration)
        binding.navView.setupWithNavController(findNavController())

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

    private fun setSortGroupButtons() {
        check(binding.materialButtonToggleGroupSort.isSingleSelection)
        binding.materialButtonToggleGroupSort.addOnButtonCheckedListener {
                group, checkedId, isChecked ->
            if (isChecked) {
                val checkedButton = findViewById<MaterialButton>(checkedId)
                check(checkedButton != null)

                val sortValue = MediaSort.valueOf(checkedButton.tag.toString())
                Timber.e("New sort value: $sortValue")

                getMediaListFragment().search(sortValue)
            }
        }
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
    }

    private fun getMediaListFragment(): MediaListFragment {
        val navHost = supportFragmentManager.primaryNavigationFragment // NavHostFragment
        val currentFragment = navHost?.childFragmentManager?.primaryNavigationFragment
        return currentFragment as MediaListFragment
    }
}
