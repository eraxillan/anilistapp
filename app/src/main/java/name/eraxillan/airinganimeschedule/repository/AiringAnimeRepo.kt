package name.eraxillan.airinganimeschedule.repository

import android.content.Context
import androidx.lifecycle.LiveData
import name.eraxillan.airinganimeschedule.db.AiringAnimeDao
import name.eraxillan.airinganimeschedule.db.AiringAnimeDatabase
import name.eraxillan.airinganimeschedule.model.AiringAnime

/**
 * Repository pattern implementation: make an independent from concrete data source wrapper
 */
class AiringAnimeRepo(context: Context) {
    private var db = AiringAnimeDatabase.getInstance(context)
    private var airingAnimeDao: AiringAnimeDao = db.ongoingDao()

    fun addOngoing(anime: AiringAnime): Long? {
        val newId = airingAnimeDao.insertOngoing(anime)
        anime.id = newId
        return newId
    }

    fun deleteOngoing(anime: AiringAnime) {
        airingAnimeDao.deleteOngoing(anime)
    }

    fun createOngoing(): AiringAnime {
        // TODO: add special initialization code if needed
        return AiringAnime()
    }

    val allOngoings: LiveData<List<AiringAnime>>
        get() {
            return airingAnimeDao.loadAll()
        }
}
