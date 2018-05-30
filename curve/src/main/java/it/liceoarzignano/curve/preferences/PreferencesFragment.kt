package it.liceoarzignano.curve.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import it.liceoarzignano.curve.R
import it.liceoarzignano.foundation.extensions.get
import it.liceoarzignano.foundation.util.PreferenceKeys

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        val usernamePref = findPreference(PreferenceKeys.KEY_USER_NAME)
        usernamePref.summary = preferences[PreferenceKeys.KEY_USER_NAME,
                getString(R.string.preferences_username_default)]
        usernamePref.setOnPreferenceChangeListener { preference, newValue ->
            preference.summary = newValue as String
            true
        }
    }
}