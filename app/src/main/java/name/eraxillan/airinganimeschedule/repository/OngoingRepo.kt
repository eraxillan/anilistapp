package name.eraxillan.airinganimeschedule.repository

import android.content.Context
import androidx.lifecycle.LiveData
import name.eraxillan.airinganimeschedule.db.OngoingDao
import name.eraxillan.airinganimeschedule.db.OngoingDatabase
import name.eraxillan.airinganimeschedule.model.AiringAnime

/**
 * Repository pattern implementation: make an independent from concrete data source wrapper
 */
class OngoingRepo(context: Context) {
    private var db = OngoingDatabase.getInstance(context)
    private var ongoingDao: OngoingDao = db.ongoingDao()

    fun addOngoing(anime: AiringAnime): Long? {
        val newId = ongoingDao.insertOngoing(anime)
        anime.id = newId
        return newId
    }

    fun deleteOngoing(anime: AiringAnime) {
        ongoingDao.deleteOngoing(anime)
    }

    fun createOngoing(): AiringAnime {
        // TODO: add special initialization code if needed
        return AiringAnime()
    }

    val allOngoings: LiveData<List<AiringAnime>>
        get() {
            return ongoingDao.loadAll()
        }
}
