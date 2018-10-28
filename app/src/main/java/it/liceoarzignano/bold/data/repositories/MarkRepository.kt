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
import it.liceoarzignano.bold.data.dao.MarkDao
import it.liceoarzignano.bold.data.entities.Mark
import it.liceoarzignano.bold.utilities.SingletonHolder

class MarkRepository private constructor(private val dao: MarkDao) {

    fun getLive(subject: Int) = dao.getLive(subject)

    fun getPaged(subject: Int) = dao.getPaged(subject)

    fun getById(uid: Long) = dao.getById(uid)

    fun insert(mark: Mark) = dao.insert(mark)

    fun delete(mark: Mark) = dao.delete(mark)

    companion object : SingletonHolder<MarkRepository, Context> ({
        MarkRepository(AppDatabase.getInstance(it).markDao())
    })
}