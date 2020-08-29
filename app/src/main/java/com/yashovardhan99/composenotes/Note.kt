package com.yashovardhan99.composenotes

import java.util.*

data class Note(
    var text: String,
    val created: Date,
    var lastModified: Date
)