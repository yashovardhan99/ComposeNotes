package com.yashovardhan99.composenotes

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class RoomConvertersTest {
    private val date = Calendar.getInstance().apply {
        set(Calendar.YEAR, 2020)
        set(Calendar.MONTH, Calendar.SEPTEMBER)
        set(Calendar.DAY_OF_MONTH, 8)
    }.time

    @Test
    fun dateToTimestamp() {
        assertEquals(date.time, Converters().toTimestamp(date))
    }

    @Test
    fun timestampToDate() {
        assertEquals(date, Converters().fromTimestamp(date.time))
    }
}