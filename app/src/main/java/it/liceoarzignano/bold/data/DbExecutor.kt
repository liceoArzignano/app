/*
 * Copyright (c) 2018 Liceo L. Da Vinci, Arzignano (VI)
 * Copyright (c) 2018 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.
 */
package it.liceoarzignano.bold.data

import java.util.concurrent.Callable
import java.util.concurrent.Executors

private val DB_EXECUTOR = Executors.newSingleThreadExecutor()

fun runOnDbThread(operations: () -> Unit) {
    DB_EXECUTOR.execute(operations)
}

fun <T> runOnDbThread(operations: () -> T): T {
    val result = DB_EXECUTOR.submit(Callable<T> { operations() })
    return result.get()
}

fun <I, O> runOnDbThread(operations: () -> I, post: (I) -> O): O {
    val result = DB_EXECUTOR.submit(Callable<I> { operations() })
    return post(result.get())
}

object DbThread {

    fun shutDown() {
        if (!DB_EXECUTOR.isShutdown) {
            DB_EXECUTOR.shutdownNow()
        }
    }
}