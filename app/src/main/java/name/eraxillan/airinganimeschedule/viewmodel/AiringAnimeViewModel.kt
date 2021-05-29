package name.eraxillan.airinganimeschedule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.parser.FakeParser
import name.eraxillan.airinganimeschedule.repository.AiringAnimeRepo
import java.net.URL

class AiringAnimeViewModel(application: Application)
    : AndroidViewModel(application) {

    companion object {
        private val LOG_TAG = AiringAnimeViewModel::class.java.simpleName
    }

    private var airingAnimeRepo: AiringAnimeRepo = AiringAnimeRepo(getApplication())
    private var ongoings: LiveData<List<AiringAnime>>? = null

    fun parseOngoingFromUrl(url: URL): AiringAnime {
        val anime = airingAnimeRepo.createOngoing()

        anime.url = url
        // FIXME IMPLEMENT: parse website using JSoup and fill all `anime` fields
        val parser = FakeParser()
        if (!parser.parse(url, anime)) {
            Log.e(LOG_TAG, "Unable to fetch anime data from URL='$url'!")
        }

        return anime
    }

    fun addOngoing(anime: AiringAnime) {
        val newId = airingAnimeRepo.addOngoing(anime)
        Log.i(LOG_TAG, "New anime with id=$newId added to the SQLite database")
    }

    fun deleteOngoing(anime: AiringAnime) {
        airingAnimeRepo.deleteOngoing(anime)
    }

    fun getOngoings(): LiveData<List<AiringAnime>>? {
        if (ongoings == null) {
            ongoings = airingAnimeRepo.allOngoings
        }
        return ongoings
    }
}
