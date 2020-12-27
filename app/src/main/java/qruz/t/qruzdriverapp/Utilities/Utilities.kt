package com.qruz.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Utilities {
    fun removeFirstChar(s: String): String {
        val c = s[0]
        return if (c == '0') {
            s.substring(1)
        } else {
            s
        }
    }

    fun convertMilliToDateTime(milli: Long): String {



        val simple: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")


        val result = Date(milli)


       return simple.format(result)

    }
}