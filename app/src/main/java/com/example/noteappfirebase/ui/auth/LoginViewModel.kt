package com.example.noteappfirebase.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteappfirebase.core.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.LoginWithGoogle -> loginWithGoogle(intent.context)
            is LoginIntent.ClearMessages -> resetMessages()
        }
    }

    private fun loginWithGoogle(context: Context) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val res = authService.loginWithGoogle(context)
                withContext(Dispatchers.Main) {
                    _state.update {
                        it.copy(
                            successMessage = if (res) "Login successful" else null,
                            errorMessage = if (!res) "Login failed" else null
                        )
                    }
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

data class LoginState(
    val isLoading: Boolean = true,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)

sealed class LoginIntent {
    class LoginWithGoogle(val context: Context) : LoginIntent()
    object ClearMessages : LoginIntent()
}