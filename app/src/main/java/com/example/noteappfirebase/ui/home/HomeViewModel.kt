package com.example.noteappfirebase.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteappfirebase.data.model.Note
import com.example.noteappfirebase.data.repo.NoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: NoteRepo
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        handleIntent(HomeIntent.GetNotes)
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.GetNotes -> getNotes()
            is HomeIntent.DeleteNote -> deleteNote(intent.id)
            is HomeIntent.ClearMessage -> resetMessages()
        }
    }

    private fun getNotes() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repo.getNotes().collect { items ->
                    _state.update { it.copy(notes = items, isLoading = false) }
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = e.message.toString(), isLoading = false) }
        }
    }

    fun deleteNote(id: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repo.deleteNote(id)

                _state.update { it.copy(successMessage = "Note deleted") }
            }
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = e.message.toString()) }
        }
    }

    fun resetMessages() {
        _state.update { it.copy(successMessage = null, errorMessage = null) }
    }
}

data class HomeState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = true,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)

sealed class HomeIntent {
    object GetNotes : HomeIntent()
    class DeleteNote(val id: String) : HomeIntent()
    object ClearMessage : HomeIntent()
}