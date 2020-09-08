package com.yashovardhan99.composenotes

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
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

    // TODO: 08/09/20 : Tests for inserting notes
    // TODO: 08/09/20 : Tests for deleting notes
    // TODO: 08/09/20 : Tests for getting all notes
    // TODO: 08/09/20 : Tests for getting specific notes
    // TODO: 08/09/20 : Tests for searching notes

    @Test
    fun sampleTest() {
        Timber.d("Starting test")
        runBlocking {
            notesDao.loadAllNotes().take(1).collect {
                Timber.d("Received list = $it")
                Assert.assertTrue(it.isEmpty())
            }
        }
    }

    @After
    fun closeDb() {
        Timber.d("Closing db")
        database.close()
    }


}