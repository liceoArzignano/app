/*
 * Copyright (c) 2018 Liceo L. Da Vinci, Arzignano (VI)
 * Copyright (c) 2018 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.
 */
package it.liceoarzignano.bold.data.repositories

import android.content.Context
import it.liceoarzignano.bold.data.AppDatabase
import it.liceoarzignano.bold.data.dao.PingDao
import it.liceoarzignano.bold.data.entities.Ping
import it.liceoarzignano.bold.utilities.SingletonHolder

class PingsRepository private constructor(private val dao: PingDao) {

    fun getPagedList() = dao.getPagedList()

    fun insert(ping: Ping) = dao.insert(ping)

    companion object : SingletonHolder<PingsRepository, Context> ({
        PingsRepository(AppDatabase.getInstance(it).pingDao())
    })
}