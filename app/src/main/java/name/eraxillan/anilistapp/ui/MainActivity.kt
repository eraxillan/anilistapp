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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import dagger.hilt.android.AndroidEntryPoint
import name.eraxillan.anilistapp.R
import name.eraxillan.anilistapp.databinding.ActivityMainBinding
import name.eraxillan.anilistapp.ui.views.BackdropPanel
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    // This property is only valid between `onCreateView` and `onDestroyView`
    private val binding get() = _binding!!

    private lateinit var appBarConfiguration: AppBarConfiguration

    fun panel(): BackdropPanel = binding.backdropViews

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
        binding.backdropViews.show(true)
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
            binding.backdropViews.show(menuItem.itemId == 2131296544)

            val isHandled = NavigationUI.onNavDestinationSelected(menuItem, findNavController())
            if (isHandled) {
                val parent = binding.navView.parent
                if (parent is DrawerLayout)
                    parent.closeDrawer(GravityCompat.START)
            }

            // Avoid layout shift to bottom
            binding.backdropViews.openBottomSheet()

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

    private fun configureBackdrop() {
        // Show backdrop controls only on `MediaListFragment` ("Discover" menu item)
        val navHost = supportFragmentManager.primaryNavigationFragment // NavHostFragment
        val currentFragment = navHost?.childFragmentManager?.primaryNavigationFragment
        binding.backdropViews.show(navHost == null || currentFragment is MediaListFragment)

        binding.backdropViews.setupListeners { filterOptions, sortOption ->
            Timber.d("Fetching filtered and sorted media list...")
            getMediaListFragment().search(filterOptions, sortOption)
        }

        binding.backdropViews.scrollUpAction = { getMediaListFragment().scrollUp() }
    }

    private fun getMediaListFragment(): MediaListFragment {
        val navHost = supportFragmentManager.primaryNavigationFragment // NavHostFragment
        val currentFragment = navHost?.childFragmentManager?.primaryNavigationFragment
        return currentFragment as MediaListFragment
    }
}
