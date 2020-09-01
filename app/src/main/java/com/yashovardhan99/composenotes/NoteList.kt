package com.yashovardhan99.composenotes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
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
    Text(
        modifier = modifier,
        text = text,
        maxLines = 2,
        style = typography.caption,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun PrimaryText(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = if (text.isNotBlank()) text else "New note...",
        fontStyle = if (text.isNotBlank()) FontStyle.Normal else FontStyle.Italic,
        maxLines = 1,
        style = if (text.isNotBlank()) typography.subtitle1 else typography.caption,
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

class NotesProvider(count: Int = 1) : PreviewParameterProvider<Note> {
    override val values: Sequence<Note>

    init {
        val notes = mutableListOf<Note>()
        for (i in 1..count)
            notes.add(Note(i.toLong(), LoremIpsum(5 * i).values.first(), Date(), Date()))
        values = notes.asSequence()
    }
}

class NoteItemPreviewProvider :
    PreviewParameterProvider<Pair<Note, Boolean>> {
    override val values: Sequence<Pair<Note, Boolean>>

    init {
        val note = NotesProvider(1)
        values = sequenceOf(Pair(note.values.first(), false), Pair(note.values.first(), true))
    }
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
    @PreviewParameter(NoteItemPreviewProvider::class) noteItem: Pair<Note, Boolean>
) {
    ComposeNotesTheme(darkTheme = noteItem.second) {
        NoteItem(noteItem.first, {})
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