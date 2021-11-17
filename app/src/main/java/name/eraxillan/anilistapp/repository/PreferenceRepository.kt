package name.eraxillan.anilistapp.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import name.eraxillan.anilistapp.model.*
import name.eraxillan.anilistapp.utilities.*
import java.time.LocalDate


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

    var sortOption: MediaSort = MediaSort.UNKNOWN
        get() {
            val optionStr = preferences.getString(PREF_SORT_OPTION, MediaSort.UNKNOWN.name)
                ?: MediaSort.UNKNOWN.name
            return MediaSort.valueOf(optionStr)
        }
        set(value) {
            preferences.edit().putString(PREF_SORT_OPTION, value.name).apply()

            field = value
        }

    var filterOptions: MediaFilter = MediaFilter()
        get() {
            val searchStr = preferences.getString(PREF_FILTER_SEARCH_OPTION, null)
            val genresStr = preferences.getString(PREF_FILTER_GENRES_OPTION, null)
            val tagsStr = preferences.getString(PREF_FILTER_TAGS_OPTION, null)
            val yearStr = preferences.getString(PREF_FILTER_YEAR_OPTION, null)
            val seasonStr = preferences.getString(PREF_FILTER_SEASON_OPTION, null)
            val formatsStr = preferences.getString(PREF_FILTER_FORMATS_OPTION, null)
            val statusStr = preferences.getString(PREF_FILTER_STATUS_OPTION, null)
            val servicesStr = preferences.getString(PREF_FILTER_SERVICES_OPTION, null)
            val countryStr = preferences.getString(PREF_FILTER_COUNTRY_OPTION, null)
            val sourcesStr = preferences.getString(PREF_FILTER_SOURCES_OPTION, null)
            val isLicensedStr = preferences.getString(PREF_FILTER_IS_LICENSES_OPTION, null)

            val gson = Gson()
            val genres: List<String>? = gson.fromJson(genresStr, Array<String>::class.java)?.toList()
            val tags: List<String>? = gson.fromJson(tagsStr, Array<String>::class.java)?.toList()
            val year: Int? = gson.fromJson(yearStr, Int::class.java)
            val season: MediaSeason? = gson.fromJson(seasonStr, MediaSeason::class.java)
            val formats: List<MediaFormatEnum>? = gson.fromJson(formatsStr, Array<MediaFormatEnum>::class.java)?.toList()
            val status: MediaStatus? = gson.fromJson(statusStr, MediaStatus::class.java)
            val services: List<String>? =
                gson.fromJson(servicesStr, Array<String>::class.java)?.toList()
            val country: MediaCountry? = gson.fromJson(countryStr, MediaCountry::class.java)
            val sources: List<MediaSourceEnum>? = gson.fromJson(sourcesStr, Array<MediaSourceEnum>::class.java)?.toList()
            val isLicensed: Boolean? = gson.fromJson(isLicensedStr, Boolean::class.java)

            return MediaFilter(
                searchStr,
                genres,
                tags,
                year,
                season,
                formats,
                status,
                services,
                country,
                sources,
                isLicensed
            )
        }
        set(value) {
            val gson = Gson()
            preferences.edit().apply {
                putString(PREF_FILTER_GENRES_OPTION, gson.toJson(value.genres?.toTypedArray()))
                putString(PREF_FILTER_TAGS_OPTION, gson.toJson(value.tags?.toTypedArray()))
                putString(PREF_FILTER_YEAR_OPTION, gson.toJson(value.year))
                putString(PREF_FILTER_SEASON_OPTION, gson.toJson(value.season))
                putString(PREF_FILTER_FORMATS_OPTION, gson.toJson(value.formats?.toTypedArray()))
                putString(PREF_FILTER_STATUS_OPTION, gson.toJson(value.status))
                putString(PREF_FILTER_SERVICES_OPTION, gson.toJson(value.services?.toTypedArray()))
                putString(PREF_FILTER_COUNTRY_OPTION, gson.toJson(value.country))
                putString(PREF_FILTER_SOURCES_OPTION, gson.toJson(value.sources?.toTypedArray()))
                putString(PREF_FILTER_IS_LICENSES_OPTION, gson.toJson(value.isLicensed))
            }.apply()

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
