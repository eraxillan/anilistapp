package name.eraxillan.ongoingschedule.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import name.eraxillan.ongoingschedule.*
import name.eraxillan.ongoingschedule.databinding.ActivityOngoingListBinding


class OngoingListActivity : AppCompatActivity() {

    companion object {
        private val LOG_TAG = OngoingListActivity::class.java.simpleName
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Perform initialization of all fragments
    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO: do we need this call?
        //setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        val binding = ActivityOngoingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)

        // Setup toolbar for using with Navigation Component
        NavigationUI.setupActionBarWithNavController(this, findNavController())
    }

    override fun onSupportNavigateUp(): Boolean {
        // return currentNavController?.value?.navigateUp() ?: false
        return findNavController().navigateUp()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun findNavController(): NavController {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host) as NavHostFragment
        return navHostFragment.navController
    }
}
