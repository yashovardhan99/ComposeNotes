package com.yashovardhan99.composenotes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomAppBar
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.yashovardhan99.composenotes.ui.ComposeNotesTheme
import com.yashovardhan99.composenotes.ui.typography
import dev.chrisbanes.accompanist.coil.CoilImage
import timber.log.Timber

@ExperimentalLayout
@ExperimentalFocus
@ExperimentalFoundationApi
@Composable
fun NoteEditor(
    originalNote: Note,
    updateNote: (Note, String) -> Note,
    modifier: Modifier = Modifier
) {
    Timber.d("NoteEditor : Note = $originalNote")
    var note by remember { mutableStateOf(originalNote) }
    var value by savedInstanceState(saver = TextFieldValue.Saver) {
        TextFieldValue(
            note.text,
            selection = TextRange(note.text.length)
        )
    }
    val scrollState = rememberScrollState()
    val focusRequester = FocusRequester()
    // This behaviour might be fixed depending on future versions of compose
    ScrollableColumn(
        modifier = modifier.fillMaxSize()
    ) {
        note.imageUri?.let {
            CoilImage(
                data = it,
                modifier = Modifier.gravity(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
            )
        }
        BaseTextField(
            visualTransformation = firstLineHighlight,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.NoAction,
            value = value,
            onValueChange = {
                if (value.text != it.text)
                    note = updateNote(note, it.text)
                value = it
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 30.dp)
                .focusRequester(focusRequester)
        )
    }
    onCommit {
        if (note.text.isBlank())
            focusRequester.requestFocus()
    }
}

@Composable
fun EditScaffold(
    onBackPressed: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onRequestCamera: () -> Unit,
    bottomText: String = "",
    hasCamera: Boolean = true,
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
                elevation = 2.dp, actions = {
                    IconButton(onClick = onDelete) {
                        Icon(asset = Icons.Default.Delete)
                    }
                    IconButton(onClick = onShare) {
                        Icon(asset = Icons.Default.Share)
                    }
                }
            )
        }, bottomBar = {
            BottomAppBar(backgroundColor = Color.Transparent, elevation = 2.dp) {
                if (hasCamera)
                    IconButton(onClick = onRequestCamera) {
                        Icon(asset = Icons.Default.CameraAlt)
                    }
                IconButton(onClick = {}) {
                    Icon(asset = Icons.Default.Image)
                }
                Text(
                    text = bottomText,
                    style = typography.caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.gravity(Alignment.CenterVertically).weight(1f)
                )
            }
        })
}

@ExperimentalLayout
@ExperimentalFocus
@ExperimentalFoundationApi
@Preview
@Composable
fun NoteEditorPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    val note = NotesProvider(wordIncrement = 50).values.first()
    ComposeNotesTheme(darkTheme = isDark) {
        EditScaffold(
            onBackPressed = {},
            onDelete = {},
            onShare = {},
            bottomText = "Edit here",
            onRequestCamera = {}) {
            NoteEditor(originalNote = note, updateNote = { _, _ -> note })
        }
    }
}

val firstLineHighlight = object : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val first = text.text.indexOf('\n')
        val builder = AnnotatedString.Builder(text)
        val style = SpanStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
        val lineStyle = ParagraphStyle(lineHeight = 36.sp)
        val defaultStyle = ParagraphStyle(lineHeight = 24.sp)
        builder.addStyle(style, 0, if (first != -1) first else text.length)
        builder.addStyle(lineStyle, 0, if (first != -1) first else text.length)
        if (first != -1)
            builder.addStyle(defaultStyle, first, text.length)
        return TransformedText(builder.toAnnotatedString(), OffsetMap.identityOffsetMap)
    }

}