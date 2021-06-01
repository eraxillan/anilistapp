package name.eraxillan.airinganimeschedule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.parser.FakeParser
import name.eraxillan.airinganimeschedule.repository.AiringAnimeRepo
import name.eraxillan.airinganimeschedule.ui.showAiringAnimeInfo
import java.lang.Thread.currentThread
import java.net.URL

class AiringAnimeViewModel(application: Application)
    : AndroidViewModel(application) {

    companion object {
        private val LOG_TAG = AiringAnimeViewModel::class.java.simpleName
    }

    private var airingAnimeRepo: AiringAnimeRepo = AiringAnimeRepo(getApplication())
    private var airingAnimes: LiveData<List<AiringAnime>>? = null

    private fun parseAiringAnimeFromUrl(url: URL): AiringAnime {
        val anime = airingAnimeRepo.createAiringAnime()

        anime.url = url
        // FIXME IMPLEMENT: parse website using JSoup and fill all `anime` fields
        val parser = FakeParser()
        if (!parser.parse(url, anime)) {
            Log.e(LOG_TAG, "Unable to fetch anime data from URL='$url'!")
        }

        return anime
    }

    fun addAiringAnime(url: URL, navController: NavController) {
        /*val job =*/ viewModelScope.launch {
            // Parse airing anime data from website
            val anime = parseAiringAnimeFromUrl(url)

            // Save airing anime to database
            val newId = airingAnimeRepo.addAiringAnime(anime)
            Log.i(LOG_TAG, "New anime with id=$newId added to the SQLite database")

            withContext(Dispatchers.Main) {
                showAiringAnimeInfo(anime, navController)
            }
        }
        //job.cancelAndJoin()
    }

    fun deleteAiringAnime(anime: AiringAnime) {
        /*val job =*/ viewModelScope.launch {
            airingAnimeRepo.deleteAiringAnime(anime)
        }
        //job.cancelAndJoin()
    }

    fun getAiringAnimes(): LiveData<List<AiringAnime>>? {
        if (airingAnimes == null) {
            airingAnimes = airingAnimeRepo.airingAnimeList
        }
        return airingAnimes
    }
}
