package name.eraxillan.ongoingschedule.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import name.eraxillan.ongoingschedule.*
import name.eraxillan.ongoingschedule.databinding.ActivityOngoingListBinding
import name.eraxillan.ongoingschedule.model.Ongoing


class OngoingListActivity
    : AppCompatActivity()
    , OngoingListFragment.OnOngoingInfoFragmentInteractionListener {

    companion object {
        const val INTENT_ONGOING_KEY = "ongoing"
        private val LOG_TAG = OngoingListActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityOngoingListBinding
    private var ongoingDetailsFragment: OngoingDetailsFragment? = null
    private var largeScreen = false

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showOngoingInfo(ongoing: Ongoing) {
        title = ongoing.originalName

        val id = if (largeScreen) R.id.fragment_container else R.id.nav_host_fragment_container_view

        ongoingDetailsFragment = OngoingDetailsFragment.newInstance(ongoing)

        ongoingDetailsFragment?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    id, it,
                    getString(R.string.ongoing_detail_fragment_tag)
                )
                .addToBackStack(null)
                .commit()
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
        largeScreen = (binding.contentMain.fragmentContainer != null)
    }

    // Called when the activity has detected the user's press of the back key
    override fun onBackPressed() {
        super.onBackPressed()

        title = resources.getString(R.string.app_name)

        ongoingDetailsFragment?.let {
            supportFragmentManager
                .beginTransaction()
                .remove(it)
                .commit()
            ongoingDetailsFragment = null
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onOngoingAdded(ongoing: Ongoing) {
        showOngoingInfo(ongoing)
    }

    override fun onOngoingClicked(ongoing: Ongoing) {
        showOngoingInfo(ongoing)
    }
}
