package com.example.noteappfirebase.data.repo

import com.example.noteappfirebase.data.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepo {
    fun getNotes(): Flow<List<Note>>

    suspend fun getNoteById(id: String): Note?

    suspend fun addNote(note: Note)

    suspend fun deleteNote(id: String)

    suspend fun updateNote(note: Note)
}