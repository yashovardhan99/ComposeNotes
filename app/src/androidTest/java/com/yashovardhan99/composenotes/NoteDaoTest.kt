package com.yashovardhan99.composenotes

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class NoteDaoTest {
    private lateinit var database: NoteDatabase
    private lateinit var notesDao: NoteDao

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java).build()
        notesDao = database.noteDao()
        runBlocking {
            database.noteDao().insertNote()
        }
    }

    // TODO: 08/09/20 : Tests for inserting notes
    // TODO: 08/09/20 : Tests for deleting notes
    // TODO: 08/09/20 : Tests for getting all notes
    // TODO: 08/09/20 : Tests for getting specific notes
    // TODO: 08/09/20 : Tests for searching notes

    @Test
    fun sampleTest() {
        runBlocking {
            notesDao.loadAllNotes().collect {
                Assert.assertArrayEquals(it.toTypedArray(), arrayOf())
            }
        }
    }

    @After
    fun closeDb() {
        database.close()
    }


}