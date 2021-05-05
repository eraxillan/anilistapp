package name.eraxillan.ongoingschedule.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import name.eraxillan.ongoingschedule.*
import name.eraxillan.ongoingschedule.databinding.ActivityOngoingListBinding
import name.eraxillan.ongoingschedule.model.Ongoing


class OngoingListActivity
    : AppCompatActivity()
    , OngoingListFragment.OnOngoingInfoFragmentInteractionListener {

    companion object {
        const val INTENT_ONGOING_KEY = "ongoing"
        private val TAG = OngoingListActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityOngoingListBinding
    private var ongoingListFragment: OngoingListFragment = OngoingListFragment.newInstance()
    private var ongoingDetailsFragment: OngoingDetailsFragment? = null
    private var largeScreen = false

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showOngoingInfo(ongoing: Ongoing) {
        title = ongoing.originalName

        if (!largeScreen) {
            showListControls(false)

            if (ongoingDetailsFragment == null)
                ongoingDetailsFragment = OngoingDetailsFragment.newInstance(ongoing)
            else
                ongoingDetailsFragment?.ongoing = ongoing

            ongoingDetailsFragment?.let {
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.nav_host_fragment_container_view, it,
                        getString(R.string.ongoing_detail_fragment_tag)
                    )
                    .addToBackStack(null)
                    .commit()
            }
        } else {
            ongoingDetailsFragment = OngoingDetailsFragment.newInstance(ongoing)
            ongoingDetailsFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container, it,
                        getString(R.string.ongoing_detail_fragment_tag)
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Perform initialization of all fragments
    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO: do we need this call?
        //setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        binding = ActivityOngoingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)

        // `FragmentManager` is an object that
        // lets you dynamically add and remove Fragments at runtime. This gives you a
        // powerful tool to make the UI as flexible as possible across various screen sizes.
        // It's called a support Fragment manager rather than just a Fragment manager because
        // some older versions of Android didn't include fragments
        ongoingListFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container_view) as OngoingListFragment
        largeScreen = (binding.contentMain.fragmentContainer != null)
    }

    // Initialize the contents of the Activity's standard options menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // This hook is called whenever an item in your options menu is selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            // https://developer.android.com/training/swipe/respond-refresh-request
            R.id.action_refresh -> {
                Log.i(TAG, "Refresh menu item selected")

                // Start the refresh background task.
                // This method calls `setRefreshing(false)` when it's finished
                ongoingListFragment.updateOngoingList(true)

                true
            }
            R.id.action_settings -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Called when the activity has detected the user's press of the back key
    override fun onBackPressed() {
        super.onBackPressed()

        title = resources.getString(R.string.app_name)

        // Show "Add ongoing" button and activity menu
        showListControls(true)

        ongoingDetailsFragment?.let {
            supportFragmentManager
                .beginTransaction()
                .remove(it)
                .commit()
            ongoingDetailsFragment = null
        }
    }

    private fun showListControls(show: Boolean) {
        // Show/hide "Add ongoing" button and activity menu
        //binding.fabAddOngoing.isVisible = show
        binding.toolbarMain.menu.children.forEach { it.isVisible = show }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onOngoingAdded(ongoing: Ongoing) {
        showOngoingInfo(ongoing)
    }

    override fun onOngoingClicked(ongoing: Ongoing) {
        showOngoingInfo(ongoing)
    }
}
