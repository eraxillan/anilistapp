package name.eraxillan.anilistapp.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import name.eraxillan.anilistapp.model.MediaSort
import name.eraxillan.anilistapp.utilities.PREF_IS_FIRST_RUN_KEY
import name.eraxillan.anilistapp.utilities.PREF_SORT_OPTION
import name.eraxillan.anilistapp.utilities.PREF_THEME_KEY
import name.eraxillan.anilistapp.utilities.THEME_DEFAULT_MODE


/**
 * A simple data repository for in-app settings.
 */
class PreferenceRepository private constructor(private val preferences: SharedPreferences) {

    var isFirstRun: Boolean = true
        get() {
            return preferences.getBoolean(PREF_IS_FIRST_RUN_KEY, true)
        }
        set(value) {
            preferences.edit().putBoolean(PREF_IS_FIRST_RUN_KEY, value).apply()
            field = value
        }

    val theme: String
        get() {
            return preferences.getString(PREF_THEME_KEY, THEME_DEFAULT_MODE) ?: THEME_DEFAULT_MODE
        }

    var sortOption: MediaSort = MediaSort.BY_POPULARITY
        get() {
            val optionStr = preferences.getString(PREF_SORT_OPTION, MediaSort.BY_POPULARITY.name)
                ?: MediaSort.BY_POPULARITY.name
            return MediaSort.valueOf(optionStr)
        }
        set(value) {
            preferences.edit().putString(PREF_SORT_OPTION, value.name).apply()
            field = value
        }

    // Singleton object
    companion object {
        @Volatile
        private var instance: PreferenceRepository? = null

        fun getInstance(context: Context): PreferenceRepository {
            return instance ?: synchronized(this) {
                instance ?: buildInstance(context).also { instance = it }
            }
        }

        private fun buildInstance(context: Context): PreferenceRepository {
            return PreferenceRepository(PreferenceManager.getDefaultSharedPreferences(context))
        }
    }
}
