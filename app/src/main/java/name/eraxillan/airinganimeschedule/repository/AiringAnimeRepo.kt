package name.eraxillan.airinganimeschedule.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import kotlinx.coroutines.flow.Flow
import name.eraxillan.airinganimeschedule.api.AnilistApi
import name.eraxillan.airinganimeschedule.db.AiringAnimeDao
import name.eraxillan.airinganimeschedule.db.AiringAnimeDatabase
import name.eraxillan.airinganimeschedule.db.AnilistPagingSource
import name.eraxillan.airinganimeschedule.model.AiringAnime

/**
 * Repository pattern implementation: make an independent from concrete data source wrapper
 */
class AiringAnimeRepo(context: Context) {
    private var db = AiringAnimeDatabase.getInstance(context)
    private var airingAnimeDao: AiringAnimeDao = db.airingAnimeDao()
    private val service: AnilistApi = AnilistApi.create(AnilistApi.createClient())

    // Favorite anime list local database API
    suspend fun addAiringAnime(anime: AiringAnime): Long {
        val newId = airingAnimeDao.insertAiringAnime(anime)
        anime.id = newId
        return newId
    }

    suspend fun deleteAiringAnime(anime: AiringAnime) {
        airingAnimeDao.deleteAiringAnime(anime)
    }

    val airingAnimeList: LiveData<List<AiringAnime>>
        get() {
            return airingAnimeDao.getAiringAnimeList()
        }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Currently airing anime list remote Anilist API
    val remoteAiringAnimeList: LiveData<PagingData<AiringAnime>>
        get() {
            return Pager(
                config = PagingConfig(enablePlaceholders = false, pageSize = NETWORK_PAGE_SIZE),
                pagingSourceFactory = { AnilistPagingSource(service) }
            ).liveData
        }

    //fun isAddedToFavorite(id: Int) = airingAnimeDao.isAddedToFavorite(id.toLong())
    fun isAddedToFavorite(id: Int): LiveData<Boolean> {
        val result = airingAnimeDao.isAddedToFavorite(id.toLong())

        Log.e("name.eraxillan.animeapp", "repo.isAddedToFavorite(${id.toLong()}) = ${result.value}")
        return result
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }
}
