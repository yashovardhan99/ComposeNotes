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

    @Query(
        "SELECT NOTE_TABLE.* FROM NOTE_TABLE JOIN NOTE_SEARCH ON (NOTE_TABLE.id = NOTE_SEARCH.rowid) WHERE NOTE_SEARCH MATCH :query"
    )
    fun searchNotes(query: String): Flow<List<Note>>

}