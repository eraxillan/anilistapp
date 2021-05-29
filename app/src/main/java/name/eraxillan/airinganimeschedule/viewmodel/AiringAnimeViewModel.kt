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
    private var airingAnimes: LiveData<List<AiringAnime>>? = null

    fun parseAiringAnimeFromUrl(url: URL): AiringAnime {
        val anime = airingAnimeRepo.createAiringAnime()

        anime.url = url
        // FIXME IMPLEMENT: parse website using JSoup and fill all `anime` fields
        val parser = FakeParser()
        if (!parser.parse(url, anime)) {
            Log.e(LOG_TAG, "Unable to fetch anime data from URL='$url'!")
        }

        return anime
    }

    fun addAiringAnime(anime: AiringAnime) {
        val newId = airingAnimeRepo.addAiringAnime(anime)
        Log.i(LOG_TAG, "New anime with id=$newId added to the SQLite database")
    }

    fun deleteAiringAnime(anime: AiringAnime) {
        airingAnimeRepo.deleteAiringAnime(anime)
    }

    fun getAiringAnimes(): LiveData<List<AiringAnime>>? {
        if (airingAnimes == null) {
            airingAnimes = airingAnimeRepo.allAiringAnimes
        }
        return airingAnimes
    }
}
