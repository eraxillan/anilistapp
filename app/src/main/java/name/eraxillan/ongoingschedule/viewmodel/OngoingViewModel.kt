package name.eraxillan.ongoingschedule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import name.eraxillan.ongoingschedule.model.Ongoing
import name.eraxillan.ongoingschedule.parser.FakeParser
import name.eraxillan.ongoingschedule.repository.OngoingRepo
import java.net.URL

class OngoingViewModel(application: Application)
    : AndroidViewModel(application) {

    private val TAG = OngoingViewModel::class.java.simpleName

    private var ongoingRepo: OngoingRepo = OngoingRepo(getApplication())
    private var ongoings: LiveData<List<Ongoing>>? = null

    fun parseOngoingFromUrl(url: URL): Ongoing {
        val ongoing = ongoingRepo.createOngoing()

        ongoing.url = url
        // FIXME IMPLEMENT: parse website using JSoup and fill all `ongoing` fields
        val parser = FakeParser()
        if (!parser.parse(url, ongoing)) {
            Log.e(TAG, "Unable to fetch ongoing data from URL='$url'!")
        }

        return ongoing
    }

    fun addOngoing(ongoing: Ongoing): Unit {
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
