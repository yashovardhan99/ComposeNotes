package com.yashovardhan99.composenotes

import android.os.Bundle
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
            val showList by notesViewModel.goToEdit.observeAsState()
            ComposeNotesTheme {
                if (showList == false)
                    MainPageScaffold(notesViewModel = notesViewModel) {
                        NotesList(notesViewModel, modifier = Modifier.padding(it))
                    }
                else
                    EditScaffold {
                        NoteEditor(Modifier.padding(it))
                    }
            }
        }
    }

    override fun onBackPressed() {
        if (notesViewModel.selectedNote == null)
            super.onBackPressed()
        else
            notesViewModel.selectNote(null)
    }
}

@Composable
fun MainPageScaffold(
    notesViewModel: NotesViewModel,
    content: @Composable() (InnerPadding) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomAppBar(cutoutShape = CircleShape) {}
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { notesViewModel.newNote() },
                shape = CircleShape,
                icon = { Icon(asset = Icons.Default.Add) })
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        bodyContent = content
    )
}