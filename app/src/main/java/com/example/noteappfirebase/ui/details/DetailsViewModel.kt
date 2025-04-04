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
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repo : NoteRepo
): ViewModel() {
    private val _state = MutableStateFlow(DetailsState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.FetchNote -> getNoteById(intent.id)
            is DetailsIntent.DeleteNote -> deleteNote(intent.id)
            DetailsIntent.ClearMessage -> resetMessage()
        }
    }

    private fun getNoteById(id: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val note = repo.getNoteById(id)
                if (note != null){
                    _state.update { it.copy(note = note, isLoading = false) }
                }else {
                    _state.update { it.copy(errorMessage = "Note not found", isLoading = false) }
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
                _state.update { it.copy(note = null, isLoading = false) }
            }
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = e.message.toString(), isLoading = false) }
        }
    }

    fun resetMessage() {
        _state.update { it.copy(errorMessage = null) }
    }

}

data class DetailsState(
    val note: Note? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

sealed class DetailsIntent {
    class FetchNote(val id: String): DetailsIntent()
    class DeleteNote(val id: String) : DetailsIntent()
    object ClearMessage : DetailsIntent()
}