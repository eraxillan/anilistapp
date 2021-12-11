/*
 * Copyright 2021 Aleksandr Kamyshnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package name.eraxillan.anilistapp.utilities


/**
 * Constants used throughout the app.
 */
const val DATABASE_NAME = "media_db"
//const val MEDIA_DATA_FILENAME = "medias.json"
const val GENRE_DATA_FILENAME = "genres.json"
const val TAG_DATA_FILENAME = "tags.json"
const val STUDIO_DATA_FILENAME = "studios.json"

const val NETWORK_PAGE_SIZE = 30
const val MEDIA_SEARCH_CACHE_SIZE = 32

const val NETWORK_REQUEST_RETRY_COUNT = 5
const val NETWORK_REQUEST_RETRY_INTERVAL_MS = 10_000L

const val PREF_THEME_KEY = "themePref"
const val PREF_IS_FIRST_RUN_KEY = "isFirstRun"

const val PREF_SORT_OPTION = "sortOption"

const val PREF_FILTER_SEARCH_OPTION = "filterSearchOption"
const val PREF_FILTER_GENRES_OPTION = "filterGenresOption"
const val PREF_FILTER_TAGS_OPTION = "filterTagsOption"
const val PREF_FILTER_YEAR_OPTION = "filterYearOption"
const val PREF_FILTER_SEASON_OPTION = "filterSeasonOption"
const val PREF_FILTER_FORMATS_OPTION = "filterFormatsOption"
const val PREF_FILTER_STATUS_OPTION = "filterStatusOption"
const val PREF_FILTER_SERVICES_OPTION = "filterServicesOption"
const val PREF_FILTER_COUNTRY_OPTION = "filterCountryOption"
const val PREF_FILTER_SOURCES_OPTION = "filterSourcesOption"
const val PREF_FILTER_IS_LICENSES_OPTION = "filterIsLicensedOption"

const val INIT_DATABASE_WORKER_TAG = "init_database_worker_tag"
const val INIT_DATABASE_WORKER_PROGRESS_KEY = "init_database_worker_progress_key"
