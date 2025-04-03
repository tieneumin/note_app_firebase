package com.example.noteappfirebase.ui.add

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
class AddViewModel @Inject constructor(
    private val repo: NoteRepo
) : ViewModel() {
    private val _state = MutableStateFlow(AddState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: AddIntent) {
        when (intent) {
            is AddIntent.ChangeColor -> setBackgroundColor(intent.color)
            is AddIntent.SaveNote -> addNote(intent.note)
            is AddIntent.ClearMessages -> resetMessages()
        }
    }

    private fun setBackgroundColor(color: Int) {
        _state.update { it.copy(color) }
    }

    private fun addNote(note: Note) {
        try {
            require(note.title.isNotBlank()) { "Title cannot be blank" }
            require(note.desc.isNotBlank()) { "Description cannot be blank" }
            require(note.desc.length <= 100) { "Description cannot be more than 1000 characters" }

            viewModelScope.launch(Dispatchers.IO) {
                repo.addNote(note)
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(successMessage = "Note added") }
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

data class AddState(
    val color: Int = -1,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)

sealed class AddIntent {
    class ChangeColor(val color: Int) : AddIntent()
    class SaveNote(val note: Note) : AddIntent()
    object ClearMessages : AddIntent()
}