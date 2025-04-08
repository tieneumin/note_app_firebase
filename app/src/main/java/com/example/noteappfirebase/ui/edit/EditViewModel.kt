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
    args: SavedStateHandle
) : ViewModel() {
    private val id = args.get<String>("id") ?: ""

    private val _state = MutableStateFlow(EditState())
    val state = _state.asStateFlow()

    init {
        getNoteById(id)
    }

    private fun getNoteById(id: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val note = repo.getNoteById(id)
                withContext(Dispatchers.Main) {
                    _state.update {
                        it.copy(
                            note = note,
                            errorMessage = if (note == null) "Note not found" else null,
                            isLoading = false
                        )
                    }
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = e.message.toString(), isLoading = false) }
        }
    }

    fun handleIntent(intent: EditIntent) {
        when (intent) {
            is EditIntent.ChangeColor -> setBackgroundColor(intent.color)
            is EditIntent.SaveNote -> updateNote(intent.note)
            is EditIntent.ClearMessages -> resetMessages()
        }
    }

    private fun setBackgroundColor(color: Int) = _state.update { it.copy(color = color) }
    private fun updateNote(note: Note) {
        try {
            require(note.title.isNotBlank()) { "Title cannot be blank" }
            require(note.desc.isNotBlank()) { "Description cannot be blank" }

            viewModelScope.launch(Dispatchers.IO) {
                repo.updateNote(note.copy(id = id))
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(successMessage = "Note updated") }
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = e.message.toString()) }
        }
    }

    private fun resetMessages() {
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
    object ClearMessages : EditIntent()
}