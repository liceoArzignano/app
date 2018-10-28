/*
 * Copyright (c) 2018 Liceo L. Da Vinci, Arzignano (VI)
 * Copyright (c) 2018 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.
 */
package it.liceoarzignano.bold

import com.google.android.play.core.splitcompat.SplitCompatApplication
import it.liceoarzignano.bold.data.DbThread

@Suppress("unused")
class BoldApp : SplitCompatApplication() {

    override fun onTerminate() {
        // Stop the jobs in the db thread
        DbThread.shutDown()

        super.onTerminate()
    }
}