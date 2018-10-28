/*
 * Copyright (c) 2018 Liceo L. Da Vinci, Arzignano (VI)
 * Copyright (c) 2018 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.
 */
package it.liceoarzignano.bold.viewmodels

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import com.google.android.play.core.splitinstall.SplitInstallException
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

class SplashViewModel internal constructor(
    private val splitInstallManager: SplitInstallManager,
    private val onError: (SplitInstallException) -> Unit,
    private val onSuccess: () -> Unit,
    private val onUpdate: (Int) -> Unit
): ViewModel() {

    private val listener = SplitInstallStateUpdatedListener {
        if (sessionId != it.sessionId()) {
            return@SplitInstallStateUpdatedListener
        }

        when (it.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {
                val total = it.totalBytesToDownload()
                val progress = it.bytesDownloaded()

                val percent = (progress / total * 100).toInt()


                onUpdate(when {
                    percent < 6 -> percent
                    percent in 6..9 -> 5
                    else -> percent - 5
                })
            }
            SplitInstallSessionStatus.DOWNLOADED -> onUpdate(96)
            SplitInstallSessionStatus.INSTALLING -> onUpdate(98)
            SplitInstallSessionStatus.INSTALLED -> onSuccess()
            SplitInstallSessionStatus.FAILED -> onUpdate(0)
        }
    }

    private var sessionId = 0


    fun getRequiredModules(context: Context) {
        val aManager = context.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                getSystemService(ActivityManager::class.java)
            else
                getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        }

        if (aManager.isLowRamDevice) {
            installBundle("marks", "pings")
        } else {
            installBundle("marks", "pings", "reminders")
        }
    }

    private fun installBundle(vararg modules: String) {
        val request = SplitInstallRequest.newBuilder().run {
            for (item in modules) {
                addModule(item)
            }
            build()
        }

        splitInstallManager.registerListener(listener)
        splitInstallManager.startInstall(request)
            .addOnSuccessListener { sessionId = it }
            .addOnFailureListener {
                if (it is SplitInstallException) {
                    onError(it)
                }
            }
    }


}