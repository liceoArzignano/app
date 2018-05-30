package it.liceoarzignano.foundation.help

import android.os.Bundle
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import it.liceoarzignano.foundation.BuildConfig
import it.liceoarzignano.foundation.R

class HelpFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.help)

        val howToCategory = findPreference("help_how_to") as PreferenceCategory
        val infoCategory = findPreference("help_info") as PreferenceCategory

        val versionEntry = infoCategory.findPreference("help_info_version")
        versionEntry.title = getString(R.string.help_info_version, BuildConfig.VERSION_NAME)
    }
}