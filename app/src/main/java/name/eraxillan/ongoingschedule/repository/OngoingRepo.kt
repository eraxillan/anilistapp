package name.eraxillan.ongoingschedule.repository

import android.content.Context
import androidx.lifecycle.LiveData
import name.eraxillan.ongoingschedule.db.OngoingDao
import name.eraxillan.ongoingschedule.db.OngoingDatabase
import name.eraxillan.ongoingschedule.model.Ongoing

/**
 * Repository pattern implementation: make an independent from concrete data source wrapper
 */
class OngoingRepo(context: Context) {
    private var db = OngoingDatabase.getInstance(context)
    private var ongoingDao: OngoingDao = db.ongoingDao()

    fun addOngoing(ongoing: Ongoing): Long? {
        val newId = ongoingDao.insertOngoing(ongoing)
        ongoing.id = newId
        return newId
    }

    fun createOngoing(): Ongoing {
        // TODO: add special initialization code if needed
        return Ongoing()
    }

    val allOngoings: LiveData<List<Ongoing>>
        get() {
            return ongoingDao.loadAll()
        }
}
