package name.eraxillan.ongoingschedule

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class ListDataManager(private val context: Context) {
    private val TAG = ListDataManager::class.java.simpleName

    fun saveList(list: TaskList) {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context).edit()
        sharedPreferences.putStringSet(list.name, list.tasks.toHashSet())
        sharedPreferences.apply()
    }

    fun readLists(): ArrayList<TaskList> {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val sharedPreferenceContents = sharedPreferences.all
        val taskLists = ArrayList<TaskList>()
        for (taskList in sharedPreferenceContents) {
            // FIXME: how to eliminate compiler warning at line 30?
            /*if (taskList.value == null || !isType(taskList.value, HashSet<String>())) {
                Log.e(TAG, "readLists: invalid data type in SharedPreferences were found! skipping")
                continue
            }*/

            val itemsHashSet = ArrayList(
                taskList.value as HashSet<String>
            )
            val list = TaskList(taskList.key, itemsHashSet)
            taskLists.add(list)
        }
        return taskLists
    }
}
