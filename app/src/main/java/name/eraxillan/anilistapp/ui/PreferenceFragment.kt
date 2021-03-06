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

package name.eraxillan.anilistapp.ui

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import name.eraxillan.anilistapp.R
import name.eraxillan.anilistapp.utilities.PREF_THEME_KEY
import name.eraxillan.anilistapp.utilities.applyUiTheme
import timber.log.Timber

@AndroidEntryPoint
class PreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val themePreference: ListPreference? = findPreference(PREF_THEME_KEY)
        themePreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener {
                /*preference: Preference*/ _, newValueObject: Any ->

            val newValue = newValueObject as String
            Timber.d("UI theme setting changed by user to: $newValue")

            applyUiTheme(newValue)
            true
        }
    }
}
