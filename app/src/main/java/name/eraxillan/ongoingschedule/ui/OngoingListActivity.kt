package name.eraxillan.ongoingschedule.ui

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import name.eraxillan.ongoingschedule.*
import name.eraxillan.ongoingschedule.model.Ongoing

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
    private var fragmentContainer: FrameLayout? = null
    private var largeScreen = false
    private var ongoingFragment: OngoingDetailFragment? = null

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showAddOngoingDialog() {
        val dialogTitle = getString(R.string.name_of_list)
        val positiveButtonTitle = getString(R.string.create_list)

        val builder = AlertDialog.Builder(this)
        val ongoingUrlEditText = EditText(this)
        ongoingUrlEditText.inputType = InputType.TYPE_CLASS_TEXT  // FIXME: TYPE_TEXT_VARIATION_URI ?

        builder.setTitle(dialogTitle)
        builder.setView(ongoingUrlEditText)

        builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
            ongoingSelectionFragment.addOngoing(ongoingUrlEditText.text.toString())

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
        if (!largeScreen) {
            val ongoingDetailIntent = Intent(this, OngoingDetailActivity::class.java)
            ongoingDetailIntent.putExtra(INTENT_ONGOING_KEY, ongoing)
            startActivityForResult(ongoingDetailIntent, ONGOING_INFO_REQUEST_CODE)
        } else {
            title = ongoing.originalName

            ongoingFragment = OngoingDetailFragment.newInstance(ongoing)
            ongoingFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it, getString(R.string.list_fragment_tag))
                    .addToBackStack(null)
                    .commit()
            }

            /*
            fab.setOnClickListener {
                showCreateTaskDialog()
            }
            */
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Perform initialization of all fragments
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        fab = findViewById(R.id.fab)
        fragmentContainer = findViewById(R.id.fragment_container)

        // `FragmentManager` is an object that
        // lets you dynamically add and remove Fragments at runtime. This gives you a
        // powerful tool to make the UI as flexible as possible across various screen sizes.
        // It's called a support Fragment manager rather than just a Fragment manager because
        // some older versions of Android didn't include fragments
        ongoingSelectionFragment =
            supportFragmentManager.findFragmentById(R.id.ongoing_selection_fragment) as OngoingSelectionFragment
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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Dispatch incoming result to the correct fragment
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ONGOING_INFO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                // FIXME: implement
                //ongoingSelectionFragment.saveList(data.getParcelableExtra(INTENT_LIST_KEY)!!)
            }
        }
    }

    // Called when the activity has detected the user's press of the back key
    override fun onBackPressed() {
        super.onBackPressed()

        title = resources.getString(R.string.app_name)
        // FIXME: implement
        /*
        ongoingFragment?.list?.let {
            ongoingSelectionFragment.listDataManager.saveList(it)
        }
        */
        ongoingFragment?.let {
            supportFragmentManager
                .beginTransaction()
                .remove(it)
                .commit()
            ongoingFragment = null
        }

        fab.setOnClickListener {
            showAddOngoingDialog()
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
