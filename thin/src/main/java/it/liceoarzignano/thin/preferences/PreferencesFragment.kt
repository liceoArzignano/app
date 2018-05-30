package it.liceoarzignano.thin.preferences

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceFragmentCompat
import it.liceoarzignano.thin.R

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        val switchTo = findPreference("pref_info_switch_to")

        if (isLowRam()) {
            switchTo.parent?.removePreference(switchTo)
        } else {
            switchTo.setOnPreferenceClickListener { onSwitchTo() }
        }
    }

    private fun onSwitchTo(): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(FULL_PLAY_STORE_URL))
        activity?.startActivity(intent)
        return true
    }

    private fun isLowRam() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> isLowRamApi27()
        Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT -> true
        else -> isLowRamDefault()
    }

    @RequiresApi(27)
    private fun isLowRamApi27(): Boolean {
        val manager = context?.packageManager ?: return true
        return manager.hasSystemFeature(PackageManager.FEATURE_RAM_LOW)
    }

    private fun isLowRamDefault(): Boolean {
        val manager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        manager.getMemoryInfo(memInfo)

        // The device is considered low RAM when it has less than 1gb
        return memInfo.totalMem / 1000000000 < 2
    }

    companion object {
        private const val FULL_PLAY_STORE_URL = "market://details?id=it.liceoarzignano.bold"
    }
}
