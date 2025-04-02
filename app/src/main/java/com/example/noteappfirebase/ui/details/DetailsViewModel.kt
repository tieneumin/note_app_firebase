package com.example.noteappfirebase.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteappfirebase.core.log
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
            is DetailsIntent.GetNote -> getNote(intent.id)
        }

    }

    private fun getNote(id: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val note = repo.getNote(id)
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

}

data class DetailsState(
    val note: Note? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

sealed class DetailsIntent {
    class GetNote(val id: String) : DetailsIntent()
}