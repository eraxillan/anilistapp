package name.eraxillan.ongoingschedule.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import name.eraxillan.ongoingschedule.R
import name.eraxillan.ongoingschedule.databinding.ActivityOngoingListBinding


class AiringAnimeListActivity : AppCompatActivity() {

    companion object {
        private val LOG_TAG = AiringAnimeListActivity::class.java.simpleName
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Perform initialization of all fragments
    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO: do we need this call?
        //setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        val binding = ActivityOngoingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar(binding)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setupActionBar(binding: ActivityOngoingListBinding) {
        // NOTE: see https://developer.android.com/guide/navigation/navigation-ui#action_bar

        setSupportActionBar(binding.toolbarMain)

        appBarConfiguration = AppBarConfiguration(findNavController().graph, binding.drawerLayout)
        setupActionBarWithNavController(findNavController(), appBarConfiguration)
        binding.navView.setupWithNavController(findNavController())
    }

    private fun findNavController(): NavController {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host) as NavHostFragment
        return navHostFragment.navController
    }
}
