package com.example.notesapp.data

import kotlinx.coroutines.flow.Flow


class NoteRepository(private val dao: NoteDao) {

    val allNotes: Flow<List<Note>> = dao.getAllNotes()

    suspend fun insert(note: Note) = dao.insertNote(note)

    suspend fun delete(note: Note) = dao.deleteNote(note)
}
