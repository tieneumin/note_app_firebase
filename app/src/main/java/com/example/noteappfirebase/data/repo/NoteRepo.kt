package com.example.noteappfirebase.data.repo

import com.example.noteappfirebase.data.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepo {
    fun getNotes(): Flow<List<Note>>
}