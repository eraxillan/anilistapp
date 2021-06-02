package name.eraxillan.airinganimeschedule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.parser.FakeParser
import name.eraxillan.airinganimeschedule.repository.AiringAnimeRepo
import name.eraxillan.airinganimeschedule.ui.showAiringAnimeInfo
import java.net.URL

class AiringAnimeViewModel(application: Application)
    : AndroidViewModel(application) {

    companion object {
        private const val LOG_TAG = "54BE6C87_AAVM" // AAVM = AiringAnimeViewModel
    }

    private var airingAnimeRepo: AiringAnimeRepo = AiringAnimeRepo(getApplication())
    private var airingAnimeList: LiveData<List<AiringAnime>>? = null

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

    fun getAiringAnimeList(): LiveData<List<AiringAnime>>? {
        if (airingAnimeList == null) {
            /*val job =*/ viewModelScope.launch {
                airingAnimeList = airingAnimeRepo.airingAnimeList
            }
        }
        return airingAnimeList
    }
}
