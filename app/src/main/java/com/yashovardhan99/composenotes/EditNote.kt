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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.length
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.yashovardhan99.composenotes.ui.ComposeNotesTheme
import com.yashovardhan99.composenotes.ui.typography
import timber.log.Timber

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
    var value by savedInstanceState(saver = TextFieldValue.Saver) { TextFieldValue(note.text) }
    val scrollState = rememberScrollState()
    val focusRequester = FocusRequester()
    BaseTextField(
        visualTransformation = firstLineHighlight,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.NoAction,
        value = value,
        onValueChange = {
            if (value.text != it.text)
                note = updateNote(note, it.text)
            value = it
        }, modifier = modifier.padding(horizontal = 20.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .focusRequester(focusRequester)
    )
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
                Text(
                    text = bottomText,
                    style = typography.caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.gravity(Alignment.CenterVertically).weight(1f)
                )
            }
        })
}

@ExperimentalFocus
@ExperimentalFoundationApi
@Preview
@Composable
fun NoteEditorPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    val note = NotesProvider(wordIncrement = 50).values.first()
    ComposeNotesTheme(darkTheme = isDark) {
        EditScaffold(onBackPressed = {}, onDelete = {}, onShare = {}, bottomText = "Edit here") {
            NoteEditor(originalNote = note, updateNote = { _, _ -> note })
        }
    }
}

/**
 * Changes font to monospace for any text between triple back ticks (```)
 */
val codeFilter = object : VisualTransformation {
    val style = SpanStyle(
        fontFamily = FontFamily.Monospace,
        background = Color.DarkGray
    )

    override fun filter(text: AnnotatedString): TransformedText {
        var started = false
        var count = 0
        val builder = AnnotatedString.Builder()
        val offsets = mutableListOf<Int>()
        text.text.forEachIndexed { i, char ->
            offsets.add(i - count)
            if (char == '`') {
                count += 1
            } else {
                if (count % 3 != 0) {
                    repeat(count % 3) { builder.append("`") }
                    count -= count % 3
                    builder.append(char)
                }
                builder.append(char)
            }
            if (count > 0 && count % 3 == 0 && char == '`') {
                started = started.not()
                Timber.d("Index = $i started = $started}")
                if (started) {
                    builder.pushStyle(style)
                } else {
                    builder.pop()
                }
            }
        }
        Timber.d("Offset map = $offsets")
        repeat(count % 3) { builder.append("`") }
        count -= count % 3
        val offsetMap = object : OffsetMap {
            override fun originalToTransformed(offset: Int): Int {
                return if (offset >= offsets.size) (offsets.lastOrNull() ?: offset) + 1
                else offsets[offset]
            }

            override fun transformedToOriginal(offset: Int): Int {
                return if (offsets.contains(offset))
                    offsets.indexOf(offset)
                else offset
            }
        }
        return TransformedText(builder.toAnnotatedString(), offsetMap)
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