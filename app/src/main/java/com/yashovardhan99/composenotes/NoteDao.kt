package com.yashovardhan99.composenotes

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(vararg note: Note): List<Long>

    @Update
    suspend fun updateNote(vararg note: Note)

    @Delete
    suspend fun deleteNote(vararg note: Note)

    @Query("SELECT * FROM NOTE_TABLE")
    fun loadAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM NOTE_TABLE WHERE id = :id")
    suspend fun loadNoteFromId(id: Long): Note


}