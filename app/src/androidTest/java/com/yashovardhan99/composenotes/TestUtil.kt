package com.yashovardhan99.composenotes

import java.util.*

object TestUtil {
    private val timestamp: Date = Calendar.getInstance()
        .apply {
            set(Calendar.YEAR, 2020)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DAY_OF_MONTH, 14)
        }.time
    val note1 = Note(id = 1, text = "Note 1", created = timestamp, lastModified = timestamp)
    val note2 = Note(id = 2, text = "Note 2", created = timestamp, lastModified = timestamp)
}