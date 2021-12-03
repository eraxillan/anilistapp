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


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    // This property is only valid between `onCreateView` and `onDestroyView`
    private val binding get() = _binding!!

    private lateinit var appBarConfiguration: AppBarConfiguration

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

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setupActionBar(binding: ActivityMainBinding) {
        // NOTE: see https://developer.android.com/guide/navigation/navigation-ui#action_bar

        setSupportActionBar(binding.toolbarMain)

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
}
