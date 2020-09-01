package com.yashovardhan99.composenotes

import android.os.Bundle
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.ViewModelProvider
import com.yashovardhan99.composenotes.ui.ComposeNotesTheme

@ExperimentalFoundationApi
class MainActivity : AppCompatActivity() {
    private lateinit var notesViewModel: NotesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notesViewModel = ViewModelProvider(this)[NotesViewModel::class.java]
        setContent {
            val selectedNote by notesViewModel.selectedNote.observeAsState()
            ComposeNotesTheme {
                if (selectedNote == null) {
                    notesViewModel.refreshNotes()
                    MainPageScaffold(onNewPress = { notesViewModel.newNote() }) {
                        NotesList(
                            notesViewModel.notes,
                            { note -> notesViewModel.selectNote(note) },
                            modifier = Modifier.padding(it)
                        )
                    }
                } else {
                    val note: Note? = selectedNote
                    if (note != null) {
                        EditScaffold(
                            onBackPressed =
                            { notesViewModel.selectNote(null) },
                            onDelete = { notesViewModel.deleteNote(note) },
                            bottomText = "Edited ${
                                DateUtils.getRelativeTimeSpanString(
                                    this, note.lastModified.time, false
                                )
                            }"
                        ) {
                            NoteEditor(
                                note,
                                { note, s -> notesViewModel.updateNote(note, s) },
                                Modifier.padding(it)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (notesViewModel.selectedNote.value == null)
            super.onBackPressed()
        else
            notesViewModel.selectNote(null)
    }
}

@Composable
fun MainPageScaffold(
    onNewPress: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable() (InnerPadding) -> Unit
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomAppBar(cutoutShape = CircleShape) {}
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewPress,
                shape = CircleShape,
                icon = { Icon(asset = Icons.Default.Add) })
        },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true,
        bodyContent = content
    )
}