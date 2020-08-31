package com.yashovardhan99.composenotes

import androidx.compose.foundation.BaseTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel

@ExperimentalFoundationApi
@Composable
fun NoteEditor() {
    val notesViewModel = viewModel(NotesViewModel::class.java)
    val note = notesViewModel.selectedNote ?: notesViewModel.createNote()
    var value by savedInstanceState(saver = TextFieldValue.Saver) { TextFieldValue(note.text) }
    val scrollState = rememberScrollState()
    BaseTextField(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.NoAction,
        value = value, onValueChange = {
            value = it
            notesViewModel.updateNote(note, it.text)
        },
        modifier = Modifier.fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState)
    )
}