package com.example.notesapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * MODEL layer of MVVM.
 * A single note. Room turns this class into a SQLite table called "notes".
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long
)
