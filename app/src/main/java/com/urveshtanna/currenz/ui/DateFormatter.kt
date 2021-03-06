package com.urveshtanna.currenz.ui

import java.text.SimpleDateFormat
import java.util.*

class DateFormatter {

    companion object{

        val DATE_FORMAT_DD_MM_YYYY_HH_MM = "dd-MM-yyyy HH:mm a"

        /**
         * This function return formatted time
         * @param timeInMills time to be formatted
         * @param toFormat format time needs to be converted to
         */
        fun formattedTime(
            timeInMills: Long?,
            toFormat: String?
        ): String? {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMills!!
            val formatter: SimpleDateFormat
            formatter = SimpleDateFormat(toFormat, Locale.ENGLISH)
            return formatter.format(calendar.time)
        }

    }
}