package com.yashovardhan99.composenotes

import android.os.Bundle
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.yashovardhan99.composenotes.ui.ComposeNotesTheme

@ExperimentalFocus
@ExperimentalFoundationApi
class MainActivity : AppCompatActivity() {
    private lateinit var notesViewModel: NotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notesViewModel = ViewModelProvider(this)[NotesViewModel::class.java]
        setContent {
            val selectedNote by notesViewModel.selectedNote.observeAsState()
            ComposeNotesTheme {
                Crossfade(selectedNote) { note ->
                    if (note == null) {
                        MainPageScaffold(
                            onNewPress = { notesViewModel.newNote() },
                            onSortKeyUpdate = notesViewModel::updateSortKey
                        ) { innerPadding ->
                            val notes by notesViewModel.notes.collectAsState(listOf())
                            NotesList(
                                notes,
                                { note -> notesViewModel.selectNote(note) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    } else {
                        var lastModified by mutableStateOf(note.lastModified)
                        EditScaffold(
                            onBackPressed =
                            { notesViewModel.selectNote(null) },
                            onDelete = { notesViewModel.deleteNote(note) },
                            bottomText = "Edited ${
                                DateUtils.getRelativeTimeSpanString(
                                    this, lastModified.time, false
                                )
                            }"
                        ) {
                            NoteEditor(
                                originalNote = note,
                                updateNote = { note, s ->
                                    val updated = notesViewModel.updateNote(note, s)
                                    lastModified = updated.lastModified
                                    updated
                                },
                                modifier = Modifier.padding(it)
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
    onSortKeyUpdate: (SortKey) -> Unit,
    content: @Composable() (InnerPadding) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Compose Notes") },
                actions = {
                    var dropDownState by remember { mutableStateOf(false) }
                    DropdownMenu(
                        toggle = {
                            IconToggleButton(checked = dropDownState, onCheckedChange = {
                                dropDownState = it
                            }) {
                                Icon(asset = Icons.Default.MoreVert)
                            }
                        }, expanded = dropDownState,
                        onDismissRequest = { dropDownState = false },
                        dropdownOffset = Position((-48).dp, 0.dp)
                    ) {
                        DropdownMenuItem(onClick = {
                            onSortKeyUpdate(SortKey.LAST_MODIFIED)
                            dropDownState = false
                        }) {
                            Text(text = "Sort by last modified")
                        }
                        DropdownMenuItem(onClick = {
                            onSortKeyUpdate(SortKey.CREATED)
                            dropDownState = false
                        }) {
                            Text(text = "Sort by Creation date")
                        }
                    }
                }
            )
        },
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewPress,
                shape = CircleShape,
                icon = { Icon(asset = Icons.Default.Add) })
        },
        floatingActionButtonPosition = FabPosition.End,
        bodyContent = content
    )
}