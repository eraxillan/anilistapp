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

package name.eraxillan.customviews

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager


/**
 * A simple data repository for in-app settings.
 */
class PreferenceRepository private constructor(private val preferences: SharedPreferences) {

    var isFilterCollapsed: Boolean = false
    get() {
            return preferences.getBoolean(PREF_IS_COLLAPSED_OPTION, false)
        }
        set(value) {
            preferences.edit().putBoolean(PREF_IS_COLLAPSED_OPTION, value).apply()
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
