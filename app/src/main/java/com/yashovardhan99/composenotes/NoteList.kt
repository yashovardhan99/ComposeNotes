package com.yashovardhan99.composenotes

import androidx.annotation.IntRange
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import androidx.ui.tooling.preview.datasource.LoremIpsum
import com.yashovardhan99.composenotes.ui.ComposeNotesTheme
import com.yashovardhan99.composenotes.ui.typography
import java.util.*

@Composable
fun SecondaryText(text: String, modifier: Modifier = Modifier) {
    ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
        Text(
            modifier = modifier,
            text = text,
            maxLines = 2,
            style = typography.caption,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun PrimaryText(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = if (text.isNotBlank()) text else "New note...",
        fontStyle = if (text.isNotBlank()) FontStyle.Normal else FontStyle.Italic,
        maxLines = 1,
        fontWeight = if (text.isNotBlank()) FontWeight.SemiBold else null,
        style = if (text.isNotBlank()) typography.h6 else typography.caption,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun NoteItem(note: Note, onClick: (Note) -> Unit, modifier: Modifier = Modifier) {
    val first = remember(note.text) { note.text.substringBefore('\n') }
    val secondary = remember(note.text) { note.text.substringAfter('\n', "").replace('\n', ' ') }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(note) })
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        PrimaryText(text = first)
        if (secondary.isNotBlank())
            SecondaryText(text = secondary)
    }
}

class NotesProvider(
    @IntRange(from = 1) count: Int = 1,
    @IntRange(from = 1) wordIncrement: Int = 5
) :
    PreviewParameterProvider<Note> {
    override val values: Sequence<Note>

    init {
        val notes = mutableListOf<Note>()
        for (i in 1..count)
            notes.add(
                Note(
                    i.toLong(),
                    LoremIpsum(wordIncrement * i).values.first(),
                    Date(),
                    Date()
                )
            )
        values = notes.asSequence()
    }
}

class ThemePreviewProvider :
    PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(false, true)
}

class NoteListProvider : PreviewParameterProvider<Pair<List<Note>, Boolean>> {
    private val notes = NotesProvider(10).values.toList()
    override val values: Sequence<Pair<List<Note>, Boolean>>
        get() = sequenceOf(Pair(notes, false), Pair(notes, true))
}

@ExperimentalFoundationApi
@Preview(name = "Note Item")
@Composable
fun NoteItemPreview(
    @PreviewParameter(ThemePreviewProvider::class) isDark: Boolean
) {
    val note = NotesProvider(wordIncrement = 30).values.first()
    ComposeNotesTheme(darkTheme = isDark) {
        Surface {
            NoteItem(note, {})
        }
    }
}

@Composable
fun NotesList(notes: List<Note>, onClick: (Note) -> Unit, modifier: Modifier = Modifier) {
    LazyColumnFor(items = notes, modifier = modifier) {
        NoteItem(it, onClick)
        Divider(thickness = 0.2f.dp)
    }
}

@Preview("Notes List", showDecoration = true)
@Composable
fun ListPreview(@PreviewParameter(NoteListProvider::class) noteItems: Pair<List<Note>, Boolean>) {
    ComposeNotesTheme(darkTheme = noteItems.second) {
        Scaffold {
            NotesList(noteItems.first, {}, modifier = Modifier.padding(it))
        }
    }
}