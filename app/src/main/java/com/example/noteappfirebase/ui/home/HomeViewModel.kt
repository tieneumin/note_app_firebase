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
//        handleIntent(HomeIntent.GetNotes)
        getNotes()
    }

//    private fun handleIntent(intent: HomeIntent) {
//        when (intent) {
//            HomeIntent.GetNotes -> getNotes()
//        }
//    }

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
}

data class HomeState(
    val notes: List<Note> = emptyList(),
    val errorMessage: String = "",
    val isLoading: Boolean = true,
)

//sealed class HomeIntent {
//    object GetNotes : HomeIntent()
//}