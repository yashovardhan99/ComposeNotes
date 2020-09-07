package com.yashovardhan99.composenotes

class NoteRepository(private val noteDao: NoteDao) {
    fun getAllNotes() = noteDao.loadAllNotes()
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note).first()
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    suspend fun getNote(id: Long) = noteDao.loadNoteFromId(id)
    fun searchNotes(query: String) = noteDao.searchNotes("*${query}*")
}