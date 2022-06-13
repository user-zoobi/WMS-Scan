package com.example.wms_scan.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    private const val DATE_FORMAT_6 = "yyyy-MM-dd HH:mm:ss"

    fun getCurrentDate(): String? {
        val c: Calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat(DATE_FORMAT_6, Locale.ENGLISH)
        return sdf.format(c.time)
    }
}