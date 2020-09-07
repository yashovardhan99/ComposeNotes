package com.yashovardhan99.composenotes

import org.junit.Assert
import org.junit.Test
import java.util.*

class NoteDefaultsTest {
    @Test
    fun noteDefaultValueCheck() {
        val note = Note(text = "", created = Date(), lastModified = Date())
        Assert.assertEquals(note.id, 0L)
        Assert.assertTrue(note.text.isEmpty())
    }
}
