package it.liceoarzignano.foundation.extensions

import androidx.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import java.security.SecureRandom
import java.util.*

@RunWith(AndroidJUnit4::class)
class DateTest {

    @Test
    fun diffTest() {
        val expectedDiff = SecureRandom().nextInt(100 - 0 + 1)
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_YEAR] += expectedDiff

        val a = calendar.time
        val b = Date()

        assert(Math.abs(a.diff(b)) == expectedDiff)
    }

    @Test
    fun todayTest() {
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_YEAR] += 1
        val a = calendar.time
        assert(!a.isToday())

        val b = Date()
        assert(b.isToday())
    }
}