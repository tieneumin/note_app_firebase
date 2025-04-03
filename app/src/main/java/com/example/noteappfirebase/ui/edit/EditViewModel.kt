package com.example.noteappfirebase.ui.edit

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val repo: NoteRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id = savedStateHandle.get<String>("id") ?: ""

    private val _state = MutableStateFlow(EditState())
    val state = _state.asStateFlow()

    init {
        getNote()
    }

    fun getNote() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val note = repo.getNote(id)
                if (note != null) {
                    _state.update { it.copy(note = note, color = note.color, isLoading = false) }
                } else {
                    _state.update { it.copy(errorMessage = "Note not found", isLoading = false) }
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = e.message.toString(), isLoading = false) }
        }
    }

    fun handleIntent(intent: EditIntent) {
        when (intent) {
            is EditIntent.ChangeColor -> setBackgroundColor(intent.color)
            is EditIntent.SaveNote -> editNote(intent.note)
            is EditIntent.ClearMessage -> resetMessages()
        }
    }

    private fun setBackgroundColor(color: Int) {
        _state.update { it.copy(color = color) }
    }

    fun editNote(note: Note) {
        try {
            require(note.title.isNotBlank()) { "Title cannot be blank" }
            require(note.desc.isNotBlank()) { "Description cannot be blank" }
            require(note.desc.length <= 100) { "Description cannot be more than 1000 characters" }

            viewModelScope.launch(Dispatchers.IO) {
                repo.editNote(note.copy(id = id))
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(successMessage = "Note updated") }
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = e.message.toString()) }
        }
    }

    fun resetMessages() {
        _state.update { it.copy(successMessage = null, errorMessage = null) }
    }
}

data class EditState(
    val note: Note? = null,
    val color: Int = -1,
    val isLoading: Boolean = true,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)

sealed class EditIntent {
    class ChangeColor(val color: Int) : EditIntent()
    class SaveNote(val note: Note) : EditIntent()
    object ClearMessage : EditIntent()
}