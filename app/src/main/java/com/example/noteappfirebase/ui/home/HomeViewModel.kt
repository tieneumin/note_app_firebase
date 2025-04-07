package com.example.noteappfirebase.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteappfirebase.core.service.AuthService
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
class HomeViewModel @Inject constructor(
    private val repo: NoteRepo,
    private val authService: AuthService
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        getNotes()
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

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.DeleteNote -> deleteNote(intent.id)
            is HomeIntent.Logout -> logout()
//            is HomeIntent.LoadUserImage -> getUserImage()
            is HomeIntent.ClearMessages -> resetMessages()
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

    private fun logout() = authService.logout()
    fun getUserImage() = authService.getLoggedInUser()?.photoUrl
    private fun resetMessages() {
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
    class DeleteNote(val id: String) : HomeIntent()
    object Logout : HomeIntent()
//    object LoadUserImage : HomeIntent()
    object ClearMessages : HomeIntent()
}