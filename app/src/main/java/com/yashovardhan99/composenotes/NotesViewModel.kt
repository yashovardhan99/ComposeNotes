package com.yashovardhan99.composenotes

import android.app.Application
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    val notes: Flow<List<Note>>
    private val _selectedNote: MutableLiveData<Note> = MutableLiveData()
    val selectedNote: LiveData<Note> = _selectedNote
    private val context = application.applicationContext
    private val dataStore = context.createDataStore("notesMainList")
    private val sortPreferencesKey = preferencesKey<String>("sort_type")

    init {
        val notesDao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(notesDao)
        val sortedBy = dataStore.data.map { preferences ->
            SortKey.valueOf(preferences[sortPreferencesKey] ?: SortKey.LAST_MODIFIED.name)
        }
        notes = repository.getAllNotes().combine(sortedBy) { list, sortKey ->
            Timber.d("notes = ${list.size}, Sorted by $sortKey")
            list.sortedByDescending {
                when (sortKey) {
                    SortKey.CREATED -> it.created
                    SortKey.LAST_MODIFIED -> it.lastModified
                }
            }
        }
        val search = repository.searchNotes("t")
        viewModelScope.launch(Dispatchers.IO) {
            search.collect {
                Timber.d("$it")
            }
        }
    }

    fun selectNote(note: Note?) {
        _selectedNote.value = note
    }

    fun updateNote(note: Note, text: String): Note {
        note.text = text
        note.lastModified = Date()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
        }
        return note
    }

    fun newNote() {
        val note = createNote()
        Timber.d("New note = $note")
        viewModelScope.launch {
            note.id = withContext(Dispatchers.IO) { repository.insertNote(note) }
            _selectedNote.value = note
            Timber.d("Inserted note = $note")
        }
    }

    private fun createNote(): Note {
        return Note(text = "", created = Date(), lastModified = Date())
    }

    fun deleteNote(note: Note) {
        selectNote(null)
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
        }
    }

    fun updateSortKey(sortKey: SortKey) {
        Timber.d("update sort key to $sortKey")
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[sortPreferencesKey] = sortKey.name
            }
        }
    }
}

enum class SortKey {
    LAST_MODIFIED,
    CREATED
}