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
import it.liceoarzignano.bold.data.dao.ReminderDao
import it.liceoarzignano.bold.data.entities.Reminder
import it.liceoarzignano.bold.utilities.SingletonHolder

class ReminderRepository private constructor(private val dao: ReminderDao) {

    fun getLive() = dao.getLive()

    fun getPaged() = dao.getPaged()

    fun getById(uid: Long) = dao.getById(uid)

    fun insert(reminder: Reminder) = dao.insert(reminder)

    fun delete(reminder: Reminder) = dao.delete(reminder)

    companion object : SingletonHolder<ReminderRepository, Context> ({
        ReminderRepository(AppDatabase.getInstance(it).reminderDao())
    })
}