package com.example.noteappfirebase.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteappfirebase.data.model.Note
import com.example.noteappfirebase.data.repo.NoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val repo: NoteRepo
) : ViewModel() {

    fun addNewNote(note: Note) {
        viewModelScope.launch {
            repo.addNote(note)
        }
    }



}