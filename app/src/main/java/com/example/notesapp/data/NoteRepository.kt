package com.example.notesapp.data

import kotlinx.coroutines.flow.Flow

/**
 * MODEL layer of MVVM.
 * Sits between the ViewModel and the DAO. Right now it just forwards calls,
 * but this is the layer where you'd add Firebase/Retrofit calls later
 * without the ViewModel or UI needing to change at all.
 */
class NoteRepository(private val dao: NoteDao) {

    val allNotes: Flow<List<Note>> = dao.getAllNotes()

    suspend fun insert(note: Note) = dao.insertNote(note)

    suspend fun delete(note: Note) = dao.deleteNote(note)
}
