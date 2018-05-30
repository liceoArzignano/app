package it.liceoarzignano.foundation.extensions

import android.content.res.Resources
import it.liceoarzignano.foundation.R
import java.text.SimpleDateFormat
import java.util.*

fun Date.diff(other: Date): Int {
    val a = getCalendar()
    val b = other.getCalendar()
    return (a[Calendar.YEAR] - b[Calendar.YEAR]) * 365 +
            a[Calendar.DAY_OF_YEAR] - b[Calendar.DAY_OF_YEAR]
}

fun Date.isToday(): Boolean {
    val calendar = getCalendar()
    val today = Calendar.getInstance()
    return calendar[Calendar.YEAR] == today[Calendar.YEAR] &&
            calendar[Calendar.DAY_OF_YEAR] == today[Calendar.DAY_OF_YEAR]
}

fun Date.getWeekDay(): String = format("EEEE")

fun Date.format(format: String): String =
        SimpleDateFormat(format, Locale.getDefault()).format(this)

fun Date.parse(input: String, format: String) {
    this.time = SimpleDateFormat(format, Locale.getDefault()).parse(input).time
}

fun Date.parse(year: Int, month: Int, day: Int) {
    val calendar = getCalendar()
    calendar[Calendar.YEAR] = year
    calendar[Calendar.MONTH] = month
    calendar[Calendar.DAY_OF_MONTH] = day
    time = calendar.timeInMillis
}

fun Date.getYMD(): Array<Int> {
    val calendar = getCalendar()
    return arrayOf(calendar[Calendar.YEAR],
            calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
}

fun Date.getMonthX() = getCalendar()[Calendar.MONTH] + 1

fun Date.getHeader(res: Resources): Pair<String , String> {
    val diff = diff(Date())
    val title = when {
        diff == 0 -> res.getString(R.string.time_today)
        diff == 1 -> res.getString(R.string.time_tomorrow)
        diff == -1 -> res.getString(R.string.time_yesterday)
        diff in 2..6 -> res.getString(R.string.time_next_x, getWeekDay())
        diff in -6..0-> res.getString(R.string.time_last_x, getWeekDay())
        diff > 6 -> res.getString(R.string.time_in_x_y, (diff / 7) + 1, getWeekDay())
        diff < -6 -> res.getString(R.string.time_x_y_ago, (diff / -7) + 1, getWeekDay())
        else -> "???"
    }

    val description = format(res.getString(R.string.time_day_month_short_format))

    return Pair(title, description)
}

private fun Date.getCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}