package com.yashovardhan99.composenotes

import android.app.Application
import android.net.Uri
import android.os.Environment
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@FlowPreview
@ExperimentalCoroutinesApi
class NotesViewModel @ViewModelInject constructor(
    private val repository: NoteRepository,
    application: Application
) :
    AndroidViewModel(application) {
    val notes: Flow<List<Note>>
    private val _selectedNote: MutableLiveData<Note> = MutableLiveData()
    val selectedNote: LiveData<Note> = _selectedNote
    private val context = application.applicationContext
    private val dataStore = context.createDataStore("notesMainList")
    private val sortPreferencesKey = preferencesKey<String>("sort_type")

    @ExperimentalCoroutinesApi
    private val deletedChannel = ConflatedBroadcastChannel<Note>()

    val deleteFlow = deletedChannel.asFlow()
    private val picturesDir = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    private val dateFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    init {
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
    }

    fun selectNote(note: Note?) {
        _selectedNote.value = note
    }

    fun updateNote(note: Note, imageUri: Uri): Note {
        note.imageUri = imageUri
        note.lastModified = Date()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
        }
        return note
    }

    fun updateNote(note: Note, text: String): Note {
        note.text = text
        note.lastModified = Date()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
        }
        return note
    }

    fun newNote(text: String = "") {
        val note = createNote(text)
        Timber.d("New note = $note")
        viewModelScope.launch {
            note.id = withContext(Dispatchers.IO) { repository.insertNote(note) }
            _selectedNote.value = note
            Timber.d("Inserted note = $note")
        }
    }

    fun createImageFile(): File {
        val timestamp = dateFormatter.format(Date())
        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            picturesDir
        )
    }

    private fun createNote(text: String = ""): Note {
        return Note(text = text, created = Date(), lastModified = Date())
    }

    fun deleteNote(note: Note) {
        selectNote(null)
            viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Deleting $note")
            deletedChannel.offer(note)
            Timber.d("Sent to channel")
            repository.deleteNote(note)
            Timber.d("Note deleted")
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

    fun undoDelete(note: Note) {
        note.lastModified = Date()
        viewModelScope.launch {
            repository.insertNote(note)
        }
    }

    override fun onCleared() {
        deletedChannel.close()
    }
}

enum class SortKey {
    LAST_MODIFIED,
    CREATED
}