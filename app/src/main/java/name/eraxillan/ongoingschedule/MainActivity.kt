package name.eraxillan.ongoingschedule

import android.os.Bundle
import android.text.InputType
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    // You use the `lateinit` keyword to tell the compiler that a `RecyclerView` will be
    // created sometime in the future.
    private lateinit var fab: FloatingActionButton
    private lateinit var lstOngoings: RecyclerView
    private val listDataManager: ListDataManager = ListDataManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        fab = findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener { view ->
            /*
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
             */
            showCreateListDialog()
        }

        // Setup the list view (`RecyclerView`): give it a LayoutManager and Adapter
        lstOngoings = findViewById(R.id.lstOngoings)
        // NOTE: Android also provides the `GridLayoutManager` and `StaggeredGridLayoutManager`:
        // https://developer.android.com/guide/topics/ui/layout/recyclerview#modifying-layout
        lstOngoings.layoutManager = LinearLayoutManager(this)
        val lists = listDataManager.readLists()
        lstOngoings.adapter = ListSelectionRecyclerViewAdapter(lists)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showCreateListDialog() {
        val dialogTitle = getString(R.string.name_of_list)
        val positiveButtonTitle = getString(R.string.create_list)

        val builder = AlertDialog.Builder(this)
        val listTitleEditText = EditText(this)
        listTitleEditText.inputType = InputType.TYPE_CLASS_TEXT  // FIXME: TYPE_TEXT_VARIATION_URI ?
        builder.setTitle(dialogTitle)
        builder.setView(listTitleEditText)

        builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
            val list = TaskList(listTitleEditText.text.toString())
            listDataManager.saveList(list)
            val recyclerAdapter = lstOngoings.adapter as ListSelectionRecyclerViewAdapter
            recyclerAdapter.addList(list)

            dialog.dismiss()
        }

        builder.create().show()
    }
}