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
    private var airingAnimeDao: AiringAnimeDao = db.airingAnimeDao()

    fun addAiringAnime(anime: AiringAnime): Long? {
        val newId = airingAnimeDao.insertAiringAnime(anime)
        anime.id = newId
        return newId
    }

    fun deleteAiringAnime(anime: AiringAnime) {
        airingAnimeDao.deleteAiringAnime(anime)
    }

    fun createAiringAnime(): AiringAnime {
        // TODO: add special initialization code if needed
        return AiringAnime()
    }

    val allAiringAnimes: LiveData<List<AiringAnime>>
        get() {
            return airingAnimeDao.loadAll()
        }
}
