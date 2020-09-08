package com.yashovardhan99.composenotes

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class NoteDaoTest {
    private lateinit var database: NoteDatabase
    private lateinit var notesDao: NoteDao

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java).build()
        notesDao = database.noteDao()
        Timber.d("Created database")
    }

    /**
     * Test to verify that the database is empty when initialized
     */
    @Test
    fun verifyEmpty() {
        Timber.d("Starting test")
        runBlocking {
            notesDao.loadAllNotes().take(1).collect {
                Timber.d("Received list = $it")
                assertTrue(it.isEmpty())
            }
        }
    }

    /**
     * Test to verify notes are being inserted properly
     */
    @Test
    fun verifyInserted() {
        runBlocking {
            notesDao.insertNote(TestUtil.note1)
            notesDao.insertNote(TestUtil.note2)
            notesDao.loadAllNotes().take(1).collect {
                assertEquals(it.size, 2)
                assertEquals(it[0], TestUtil.note1)
                assertEquals(it[1], TestUtil.note2)
            }
        }
    }

    @Test
    fun verifyDeleted() {
        runBlocking {
            notesDao.insertNote(TestUtil.note1, TestUtil.note2)
            notesDao.deleteNote(TestUtil.note1)
            notesDao.loadAllNotes().take(1).collect {
                assertEquals(1, it.size)
                assertEquals(TestUtil.note2, it[0])
            }
        }
    }
    // TODO: 08/09/20 : Tests for getting all notes
    // TODO: 08/09/20 : Tests for getting specific notes
    // TODO: 08/09/20 : Tests for searching notes

    @After
    fun closeDb() {
        Timber.d("Closing db")
        database.close()
    }


}