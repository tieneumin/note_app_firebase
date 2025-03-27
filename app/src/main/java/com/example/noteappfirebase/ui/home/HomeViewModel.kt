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
        getNotes()
    }

//    private fun handleIntent(intent: HomeIntent) {
//        when (intent) {
//            HomeIntent.GetNotes -> getNotes()
//        }
//    }

    private fun getNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getNotes().collect { items ->
                _state.update { it.copy(notes = items) }
            }
        }
    }
}

// intent => user interaction; getNotes() inits w/o user interaction .'. commented out
// sealed class HomeIntent {
//    data object GetNotes : HomeIntent()
//}

data class HomeState(
    val notes: List<Note> = emptyList()
)