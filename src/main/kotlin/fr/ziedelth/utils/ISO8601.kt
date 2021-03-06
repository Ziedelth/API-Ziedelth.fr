package fr.ziedelth.utils

import java.text.SimpleDateFormat
import java.util.*

object ISO8601 {
    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

    fun toUTCDateString(iso8601calendar: Calendar?): String {
        this.sdf.timeZone = TimeZone.getTimeZone("UTC")
        return this.sdf.format(Date.from(iso8601calendar?.toInstant()))
    }

    fun fromUTCDate(iso8601string: String?): Calendar? {
        if (iso8601string.isNullOrBlank()) return null
        val calendar = Calendar.getInstance()
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = sdf.parse(iso8601string)
        calendar.time = date
        return calendar
    }
}