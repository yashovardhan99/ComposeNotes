package com.yashovardhan99.composenotes

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    var notes by mutableStateOf<List<Note>>(listOf())
        private set
    var selectedNote: Note? = null
        private set
    private val _goToEdit = MutableLiveData(false)
    val goToEdit: LiveData<Boolean> = _goToEdit

    init {
        val notesDao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(notesDao)
        viewModelScope.launch {
            notes = repository.getAllNotes()
        }
    }

    fun selectNote(note: Note?) {
        selectedNote = note
        _goToEdit.value = note != null
    }

    fun updateNote(note: Note, text: String) {
        note.text = text
        note.lastModified = Date()

        viewModelScope.launch {
            repository.updateNote(note)
            notes = repository.getAllNotes()
        }
    }

    fun newNote() {
        val note = createNote()
        Timber.d("New note = $note")
        viewModelScope.launch {
            note.id = repository.insertNote(note)
            selectedNote = note
            Timber.d("Inserted note = $note")
            _goToEdit.value = true
        }
    }

    fun createNote(): Note {
        return Note(text = "", created = Date(), lastModified = Date())
    }
}