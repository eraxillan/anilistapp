package name.eraxillan.ongoingschedule.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import name.eraxillan.ongoingschedule.R

class PreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}