package name.eraxillan.ongoingschedule.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import name.eraxillan.ongoingschedule.OngoingItemsRecyclerViewAdapter
import name.eraxillan.ongoingschedule.R
import name.eraxillan.ongoingschedule.TaskList

class OngoingDetailActivity : AppCompatActivity() {
    private val TAG = OngoingDetailActivity::class.java.simpleName

    lateinit var list: TaskList
    lateinit var lstOngoingInfo: RecyclerView
    lateinit var btnAddTask: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_detail)

        list = intent.getParcelableExtra(OngoingListActivity.INTENT_LIST_KEY)!!
        title = list.name

        lstOngoingInfo = findViewById(R.id.lst_ongoing_info)
        lstOngoingInfo.adapter = OngoingItemsRecyclerViewAdapter(list)
        lstOngoingInfo.layoutManager = LinearLayoutManager(this)

        btnAddTask = findViewById(R.id.btn_add_task)
        btnAddTask.setOnClickListener {
            showCreateTaskDialog()
        }
    }

    private fun showCreateTaskDialog() {
        val taskEditText = EditText(this)
        taskEditText.inputType = InputType.TYPE_CLASS_TEXT

        AlertDialog.Builder(this)
            .setTitle(R.string.task_to_add)
            .setView(taskEditText)
            .setPositiveButton(R.string.add_task) { dialog, _ ->
                val task = taskEditText.text.toString()
                list.tasks.add(task)

                val recyclerAdapter = lstOngoingInfo.adapter as OngoingItemsRecyclerViewAdapter
                recyclerAdapter.notifyItemInserted(list.tasks.size - 1)

                dialog.dismiss()
            }
            .create()
            .show()
    }

    // Called whenever the back button is tapped to get back to the List Activity
    override fun onBackPressed() {
        // Bundle up the list in its current state, then put it into an Intent

        val bundle = Bundle()
        bundle.putParcelable(OngoingListActivity.INTENT_LIST_KEY, list)

        val intent = Intent()
        intent.putExtras(bundle)
        setResult(Activity.RESULT_OK, intent)

        super.onBackPressed()
    }
}
