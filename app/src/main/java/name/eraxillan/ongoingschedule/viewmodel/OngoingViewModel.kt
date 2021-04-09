package name.eraxillan.ongoingschedule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import name.eraxillan.ongoingschedule.model.Ongoing
import name.eraxillan.ongoingschedule.repository.OngoingRepo

class OngoingViewModel(application: Application)
    : AndroidViewModel(application) {

    private val TAG = "OngoingViewModel"

    private var ongoingRepo: OngoingRepo = OngoingRepo(getApplication())
    private var ongoings: LiveData<List<Ongoing>>? = null

    fun addOngoingFromUrl(url: String) {
        val ongoing = ongoingRepo.createOngoing()
        // FIXME IMPLEMENT: parse website using JSoup and fill all `ongoing` fields

        val newId = ongoingRepo.addOngoing(ongoing)
        Log.i(TAG, "New ongoing with id=$newId added to the SQLite database")
    }

    fun getOngoings(): LiveData<List<Ongoing>>? {
        if (ongoings == null) {
            ongoings = ongoingRepo.allOngoings
        }
        return ongoings
    }
}
