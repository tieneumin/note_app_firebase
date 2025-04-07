package com.example.noteappfirebase.core.service

import android.content.Context
import com.google.firebase.auth.FirebaseUser

interface AuthService {
    suspend fun loginWithGoogle(context: Context): Boolean
    fun logout()
    fun getLoggedInUser(): FirebaseUser?
    fun getUid(): String?
}