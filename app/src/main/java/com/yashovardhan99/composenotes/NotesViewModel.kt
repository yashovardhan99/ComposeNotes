package com.yashovardhan99.composenotes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class NotesViewModel : ViewModel() {
    private val _notes = NotesProvider(10).values.toMutableList()
    var notes by mutableStateOf<List<Note>>(listOf())
        private set
    var selectedNote: Note? = null
        private set
    private val _goToEdit = MutableLiveData(false)
    val goToEdit: LiveData<Boolean> = _goToEdit

    init {
        notes = _notes
    }

    fun selectNote(note: Note?) {
        selectedNote = note
        _goToEdit.value = note != null
    }

    fun updateNote(note: Note, text: String) {
        note.text = text
        note.lastModified = Date()
    }

    fun newNote() {
        selectedNote = createNote()
        _goToEdit.value = true
    }

    fun createNote(): Note {
        val note = Note("", Date(), Date())
        _notes.add(note)
        notes = _notes
        selectedNote = note
        return note
    }
}