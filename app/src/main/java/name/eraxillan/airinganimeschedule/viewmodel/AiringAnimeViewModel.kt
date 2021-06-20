package name.eraxillan.airinganimeschedule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.impl.utils.LiveDataUtils
import kotlinx.coroutines.launch
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.repository.AiringAnimeRepo


class AiringAnimeViewModel(application: Application)
    : AndroidViewModel(application) {

    companion object {
        private const val LOG_TAG = "54BE6C87_AAVM" // AAVM = AiringAnimeViewModel
    }

    private var airingAnimeRepo: AiringAnimeRepo = AiringAnimeRepo(getApplication())
    private var airingAnimeList: LiveData<List<AiringAnime>>? = null
    private var remoteAiringAnimeList: LiveData<PagingData<AiringAnime>>? = null

    //fun isAddedToFavorite(id: Int) = airingAnimeRepo.isAddedToFavorite(id)
    fun isAddedToFavorite(id: Int): LiveData<Boolean> {
        val result = airingAnimeRepo.isAddedToFavorite(id)
        Log.e("name.eraxillan.animeapp", "viewmodel.isAddedToFavorite($id) = ${result.value}")

        return result
    }

    fun addAiringAnime(anime: AiringAnime, navController: NavController) {
        /*val job =*/ viewModelScope.launch {
            // Save airing anime to database
            val newId = airingAnimeRepo.addAiringAnime(anime)
            Log.i(LOG_TAG, "New anime with id=$newId added to the SQLite database")

            // TODO: open `Favorites` fragment?
            /*withContext(Dispatchers.Main) {
                showAiringAnimeInfo(anime, navController)
            }*/
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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun getRemoteAiringAnimeList(): LiveData<PagingData<AiringAnime>>? {
        if (remoteAiringAnimeList == null) {
            /*val job =*/ viewModelScope.launch {
                remoteAiringAnimeList = airingAnimeRepo
                    .remoteAiringAnimeList
                    .cachedIn(viewModelScope)
            }
        }
        return remoteAiringAnimeList
    }
}
