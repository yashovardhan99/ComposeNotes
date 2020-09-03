[1mdiff --git a/app/src/main/java/com/yashovardhan99/composenotes/NoteList.kt b/app/src/main/java/com/yashovardhan99/composenotes/NoteList.kt[m
[1mindex 7aea8da..2afdf1b 100644[m
[1m--- a/app/src/main/java/com/yashovardhan99/composenotes/NoteList.kt[m
[1m+++ b/app/src/main/java/com/yashovardhan99/composenotes/NoteList.kt[m
[36m@@ -1,14 +1,18 @@[m
 package com.yashovardhan99.composenotes[m
 [m
 import androidx.annotation.IntRange[m
[31m-import androidx.compose.foundation.*[m
[31m-import androidx.compose.foundation.layout.*[m
[32m+[m[32mimport androidx.compose.foundation.ExperimentalFoundationApi[m
[32m+[m[32mimport androidx.compose.foundation.Text[m
[32m+[m[32mimport androidx.compose.foundation.clickable[m
[32m+[m[32mimport androidx.compose.foundation.layout.Arrangement[m
[32m+[m[32mimport androidx.compose.foundation.layout.Column[m
[32m+[m[32mimport androidx.compose.foundation.layout.fillMaxWidth[m
[32m+[m[32mimport androidx.compose.foundation.layout.padding[m
 import androidx.compose.foundation.lazy.LazyColumnFor[m
 import androidx.compose.material.*[m
 import androidx.compose.runtime.Composable[m
 import androidx.compose.runtime.remember[m
 import androidx.compose.ui.Alignment[m
[31m-import androidx.compose.ui.Layout[m
 import androidx.compose.ui.Modifier[m
 import androidx.compose.ui.text.font.FontStyle[m
 import androidx.compose.ui.text.font.FontWeight[m
[36m@@ -20,7 +24,6 @@[m [mimport androidx.ui.tooling.preview.PreviewParameterProvider[m
 import androidx.ui.tooling.preview.datasource.LoremIpsum[m
 import com.yashovardhan99.composenotes.ui.ComposeNotesTheme[m
 import com.yashovardhan99.composenotes.ui.typography[m
[31m-import timber.log.Timber[m
 import java.util.*[m
 [m
 @Composable[m
