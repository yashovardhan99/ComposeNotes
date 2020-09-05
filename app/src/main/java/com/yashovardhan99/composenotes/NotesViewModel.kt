package com.yashovardhan99.composenotes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    private val _notes = MutableLiveData(listOf<Note>())
    val notes: LiveData<List<Note>> = _notes
    private val _selectedNote: MutableLiveData<Note> = MutableLiveData()
    val selectedNote: LiveData<Note> = _selectedNote

    init {
        val notesDao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(notesDao)
        refreshNotes()
    }

    fun selectNote(note: Note?) {
        _selectedNote.value = note
    }

    fun updateNote(note: Note, text: String): Note {
        note.text = text
        note.lastModified = Date()
        viewModelScope.launch {
            repository.updateNote(note)
        }
        return note
    }

    fun newNote() {
        val note = createNote()
        Timber.d("New note = $note")
        viewModelScope.launch {
            note.id = repository.insertNote(note)
            _selectedNote.value = note
            Timber.d("Inserted note = $note")
        }
    }

    private fun createNote(): Note {
        return Note(text = "", created = Date(), lastModified = Date())
    }

    fun refreshNotes() {
        viewModelScope.launch {
            _notes.value = repository.getAllNotes().sortedByDescending { it.lastModified }
        }
    }

    fun deleteNote(note: Note) {
        selectNote(null)
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}