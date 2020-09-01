package com.yashovardhan99.composenotes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.yashovardhan99.composenotes.ui.ComposeNotesTheme
import com.yashovardhan99.composenotes.ui.typography
import timber.log.Timber

@ExperimentalFoundationApi
@Composable
fun NoteEditor(note: Note, updateNote: (Note, String) -> Unit, modifier: Modifier = Modifier) {
    Timber.d("NoteEditor : Note = $note")
    var value by savedInstanceState(saver = TextFieldValue.Saver) { TextFieldValue(note.text) }
    val scrollState = rememberScrollState()
    BaseTextField(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.NoAction,
        value = value, onValueChange = {
            value = it
            updateNote(note, it.text)
        },
        modifier = modifier.fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    )
}

@Composable
fun EditScaffold(
    onBackPressed: () -> Unit,
    bottomText: String = "",
    modifier: Modifier = Modifier,
    content: @Composable() (InnerPadding) -> Unit
) {
    Scaffold(modifier = modifier, bodyContent = content,
        topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(asset = Icons.Default.ArrowBack)
                }
            }, title = {}, backgroundColor = Color.Transparent,
                elevation = 2.dp
            )
        }, bottomBar = {
            BottomAppBar(backgroundColor = Color.Transparent, elevation = 2.dp) {
                Text(
                    text = bottomText,
                    style = typography.caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.gravity(Alignment.CenterVertically).weight(1f)
                )
            }
        })
}

@ExperimentalFoundationApi
@Preview
@Composable
fun NoteEditorPreview(@PreviewParameter(NoteItemPreviewProvider::class) noteItem: Pair<Note, Boolean>) {
    ComposeNotesTheme(darkTheme = noteItem.second) {
        EditScaffold(onBackPressed = {}, bottomText = "Edit here") {
            NoteEditor(note = noteItem.first, updateNote = { _, _ -> })
        }
    }

}