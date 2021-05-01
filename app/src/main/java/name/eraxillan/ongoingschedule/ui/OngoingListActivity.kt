package name.eraxillan.ongoingschedule.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import name.eraxillan.ongoingschedule.*
import name.eraxillan.ongoingschedule.model.Ongoing
import java.net.MalformedURLException
import java.net.URL
import java.util.*


/*
  Remember that splitting your code into individual, isolated Fragments makes them reusable.
  It’s essential that the Fragment needs nothing inside the Activity.
  Any app that wants to succeed across multiple devices and multiple size classes
  need to use Fragments to ensure it provides the best experience for its users.
 */

class OngoingListActivity
    : AppCompatActivity()
    , OngoingSelectionFragment.OnOngoingInfoFragmentInteractionListener {

    companion object {
        const val INTENT_ONGOING_KEY = "ongoing"
        const val ONGOING_INFO_REQUEST_CODE = 123
    }

    private val TAG = OngoingListActivity::class.java.simpleName

    // You use the `lateinit` keyword to tell the compiler that a `RecyclerView` will be
    // created sometime in the future
    private lateinit var fab: FloatingActionButton
    private var ongoingSelectionFragment: OngoingSelectionFragment = OngoingSelectionFragment.newInstance()
    private var ongoingDetailsFragment: OngoingDetailFragment? = null
    private var fragmentContainer: FrameLayout? = null
    private var largeScreen = false

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showAddOngoingDialog() {
        val dialogTitle = getString(R.string.add_ongoing_dialog_title)
        val positiveButtonTitle = getString(R.string.add_ongoing)

        val builder = AlertDialog.Builder(this)
        val ongoingUrlEditText = EditText(this)
        ongoingUrlEditText.hint = getString(R.string.add_ongoing_hint)
        ongoingUrlEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
        ongoingUrlEditText.setText(getString(R.string.invalid_url))

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!Patterns.WEB_URL.matcher(ongoingUrlEditText.text.toString()).matches())
                    ongoingUrlEditText.error = getString(R.string.invalid_ongoing_url_msg)
            }
        }
        ongoingUrlEditText.addTextChangedListener(textWatcher)

        builder.setTitle(dialogTitle)
        builder.setView(ongoingUrlEditText)

        builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
            val url: URL = try {
                URL(ongoingUrlEditText.text.toString())
            } catch (exc: MalformedURLException) {
                Toast.makeText(
                    applicationContext, getString(R.string.invalid_ongoing_url_msg), Toast.LENGTH_SHORT
                ).show()
                return@setPositiveButton
            }
            // Add ongoing to list view, db, and close dialog
            ongoingSelectionFragment.addOngoing(url)
            dialog.dismiss()
        }

        builder.create().show()
    }

    /*
    private fun showCreateTaskDialog() {
        val taskEditText = EditText(this)
        taskEditText.inputType = InputType.TYPE_CLASS_TEXT

        AlertDialog.Builder(this)
            .setTitle(R.string.task_to_add)
            .setView(taskEditText)
            .setPositiveButton(R.string.add_task) { dialog, _ ->
                val task = taskEditText.text.toString()
                ongoingFragment?.addTask(task)
                dialog.dismiss()
            }
            .create()
            .show()
    }
    */

    private fun showOngoingInfo(ongoing: Ongoing) {
        title = ongoing.originalName

        if (!largeScreen) {
            showListControls(false)

            if (ongoingDetailsFragment == null)
                ongoingDetailsFragment = OngoingDetailFragment.newInstance(ongoing)
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
            ongoingDetailsFragment = OngoingDetailFragment.newInstance(ongoing)
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ongoing_list)
        setSupportActionBar(findViewById(R.id.toolbar_main))

        fab = findViewById(R.id.fab_add_ongoing)
        fragmentContainer = findViewById(R.id.fragment_container)

        // `FragmentManager` is an object that
        // lets you dynamically add and remove Fragments at runtime. This gives you a
        // powerful tool to make the UI as flexible as possible across various screen sizes.
        // It's called a support Fragment manager rather than just a Fragment manager because
        // some older versions of Android didn't include fragments
        ongoingSelectionFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container_view) as OngoingSelectionFragment
        fragmentContainer = findViewById(R.id.fragment_container)
        largeScreen = (fragmentContainer != null)

        fab.setOnClickListener { /*view*/ _ ->
            showAddOngoingDialog()
        }
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
                ongoingSelectionFragment.updateOngoingList(true)

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

        // FIXME: implement
        /*
        ongoingFragment?.list?.let {
            ongoingSelectionFragment.listDataManager.saveList(it)
        }
        */
        ongoingDetailsFragment?.let {
            supportFragmentManager
                .beginTransaction()
                .remove(it)
                .commit()
            ongoingDetailsFragment = null
        }

        fab.setOnClickListener {
            showAddOngoingDialog()
        }
    }

    private fun showListControls(show: Boolean) {
        // Show/hide "Add ongoing" button and activity menu
        fab.isVisible = show
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        toolbar.menu.children.forEach { it.isVisible = show }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onOngoingAdded(ongoing: Ongoing) {
        showOngoingInfo(ongoing)
    }

    override fun onOngoingClicked(ongoing: Ongoing) {
        showOngoingInfo(ongoing)
    }
}
