package com.example.noteappfirebase.ui.details

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
class DetailsViewModel @Inject constructor(
    private val repo: NoteRepo
) : ViewModel() {
    private val _state = MutableStateFlow(DetailsState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.RefreshNote -> getNoteById(intent.id)
            is DetailsIntent.DeleteNote -> deleteNote(intent.id)
            is DetailsIntent.ClearMessages -> resetMessages()
        }
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

    private fun deleteNote(id: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repo.deleteNote(id)
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(successMessage = "Note deleted") }
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

data class DetailsState(
    val note: Note? = null,
    val isLoading: Boolean = true,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)

sealed class DetailsIntent {
    class RefreshNote(val id: String) : DetailsIntent()
    class DeleteNote(val id: String) : DetailsIntent()
    object ClearMessages : DetailsIntent()
}