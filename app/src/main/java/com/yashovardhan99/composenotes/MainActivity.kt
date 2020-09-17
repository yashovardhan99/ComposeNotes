package com.yashovardhan99.composenotes

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.ExperimentalLayout
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
import androidx.core.content.FileProvider
import com.yashovardhan99.composenotes.ui.ComposeNotesTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@ExperimentalLayout
@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
@ExperimentalMaterialApi
@ExperimentalFocus
@ExperimentalFoundationApi
class MainActivity : AppCompatActivity() {

    private val notesViewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("text/") == true) {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                notesViewModel.newNote(it)
            }
        }
        setContent {
            val scaffoldState = rememberScaffoldState()
            launchInComposition {
                notesViewModel.deleteFlow.collect {
                    Timber.d("Received note for snackbar : $it state = $scaffoldState")
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        "Note deleted",
                        "UNDO",
                        SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        notesViewModel.undoDelete(it)
                    }
                    Timber.d("Snackbar result = $result")
                }
            }
            val selectedNote by notesViewModel.selectedNote.observeAsState()
            Timber.d("Selected note = $selectedNote")
            ComposeNotesTheme {
                Crossfade(selectedNote) { note ->
                    if (note == null) {
                        MainPageScaffold(
                            onNewPress = { notesViewModel.newNote() },
                            onSortKeyUpdate = notesViewModel::updateSortKey,
                            state = scaffoldState
                        ) { innerPadding ->
                            val notes by notesViewModel.notes.collectAsState(listOf())
                            NotesList(
                                notes,
                                { note -> notesViewModel.selectNote(note) },
                                { note -> notesViewModel.deleteNote(note) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    } else {
                        var lastModified by mutableStateOf(note.lastModified)
                        EditScaffold(
                            onBackPressed =
                            { notesViewModel.selectNote(null) },
                            onDelete = { notesViewModel.deleteNote(note) },
                            onShare = { shareNote(note) },
                            bottomText = "Edited ${
                                DateUtils.getRelativeTimeSpanString(
                                    this, lastModified.time, false
                                )
                            }",
                            hasCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY),
                            onRequestCamera = { captureImage(note) },
                            onRequestImage = { requestImage(note) }
                        ) {
                            NoteEditor(
                                note = note,
                                updateNote = { note, s ->
                                    val updated = notesViewModel.updateNote(note, s)
                                    lastModified = updated.lastModified
                                    notesViewModel.selectNote(updated)
                                },
                                modifier = Modifier.padding(it)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestImage(note: Note) {
        val file = notesViewModel.createImageFile()
        val imageUri = FileProvider.getUriForFile(
            this,
            "com.yashovardhan99.composenotes.fileprovider",
            file
        )
        val getPicture = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            Timber.d("Got content Uri = $it")
            if (it == null) {
                file.delete()
                return@registerForActivityResult
            }
            val stream = contentResolver.openInputStream(it)
            if (stream == null) {
                file.delete()
                return@registerForActivityResult
            }
            Timber.d("Input stream = $stream")
            notesViewModel.updateNote(note, it)
            notesViewModel.saveImage(file, stream)
            notesViewModel.updateNote(note, imageUri)
        }
        getPicture.launch("image/*")
    }

    private fun captureImage(note: Note) {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) return
        val file = notesViewModel.createImageFile()
        Timber.d("File created = $file")
        val imageUri = FileProvider.getUriForFile(
            this,
            "com.yashovardhan99.composenotes.fileprovider",
            file
        )
        Timber.d("File Uri = $imageUri")
        val takePicture =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { taken ->
                Timber.d("Picture taken = $taken")
                if (taken) {
                    notesViewModel.selectNote(null)
                    notesViewModel.updateNote(note, imageUri)
                } else {
                    file.delete()
                }
            }
        takePicture.launch(imageUri)
    }

    private fun shareNote(note: Note) {
        val exclude = arrayOf(ComponentName(packageName, MainActivity::class.java.name))
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, note.text)
            type = if (note.imageUri == null)
                "text/plain"
            else {
                putExtra(Intent.EXTRA_STREAM, note.imageUri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                "image/jpeg"
            }
        }
        val shareIntent = Intent.createChooser(intent, null).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, exclude)
            }
        }
        startActivity(shareIntent)
    }

    override fun onBackPressed() {
        if (notesViewModel.selectedNote.value == null)
            super.onBackPressed()
        else
            notesViewModel.selectNote(null)
    }
}

@ExperimentalMaterialApi
@Composable
fun MainPageScaffold(
    onNewPress: () -> Unit,
    modifier: Modifier = Modifier,
    onSortKeyUpdate: (SortKey) -> Unit,
    state: ScaffoldState,
    content: @Composable() (InnerPadding) -> Unit
) {
    Scaffold(
        scaffoldState = state,
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