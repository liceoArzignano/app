/*
 * Copyright (c) 2018 Liceo L. Da Vinci, Arzignano (VI)
 * Copyright (c) 2018 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.
 */
package it.liceoarzignano.bold.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.play.core.splitinstall.SplitInstallException
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import it.liceoarzignano.bold.R
import it.liceoarzignano.bold.viewmodels.SplashViewModel
import it.liceoarzignano.bold.viewmodels.SplashViewModelFactory

class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: SplashViewModel

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = SplashViewModelFactory(
            SplitInstallManagerFactory.create(this),
            this::onError,
            this::onSuccess,
            this::update
        )
        viewModel = ViewModelProviders.of(this, factory)[SplashViewModel::class.java]

        setContentView(R.layout.activity_splash)
        progressBar = findViewById(R.id.splash_progress)
    }

    private fun onError(exception: SplitInstallException) {
        var shouldRetry = false

        val message = when (exception.errorCode) {
            SplitInstallErrorCode.NETWORK_ERROR -> getString(R.string.splash_error_network).also {
                shouldRetry = true
            }
            SplitInstallErrorCode.API_NOT_AVAILABLE -> getString(R.string.splash_error_unsupported)
            else -> getString(R.string.splash_error_unknown, exception.errorCode)
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.splash_error_title)
            .setMessage(message)
            .setPositiveButton(if (shouldRetry) R.string.action_retry else android.R.string.ok) { d, _ ->
                if (shouldRetry) {
                    d.dismiss()
                    viewModel.getRequiredModules(this)
                }
            }
            .setOnDismissListener {
                if (!shouldRetry) {
                    finish()
                }
            }
            .show()
    }

    private fun onSuccess() {
        val intent = Intent() // TODO
        startActivity(intent)
        finish()
    }

    private fun update(progress: Int) {
        progressBar.progress = progress
    }
}