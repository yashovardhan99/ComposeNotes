package com.yashovardhan99.composenotes

import android.net.Uri
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var text: String,
    var imageUri: Uri? = null,
    val created: Date,
    var lastModified: Date
)

@Fts4(contentEntity = Note::class)
@Entity(tableName = "note_search")
data class NoteFts(
    var text: String
)

class Converters {

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}