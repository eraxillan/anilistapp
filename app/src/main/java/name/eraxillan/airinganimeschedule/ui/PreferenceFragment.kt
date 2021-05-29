package name.eraxillan.airinganimeschedule.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import name.eraxillan.airinganimeschedule.R

class PreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}