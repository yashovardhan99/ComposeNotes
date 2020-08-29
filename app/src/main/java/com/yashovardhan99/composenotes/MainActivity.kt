package com.yashovardhan99.composenotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
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
                Scaffold {
                    if (showList == false)
                        NotesList(notesViewModel, modifier = Modifier.padding(it))
                    else
                        NoteEditor()
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