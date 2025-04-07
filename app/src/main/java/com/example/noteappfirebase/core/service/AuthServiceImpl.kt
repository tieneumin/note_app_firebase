package com.example.noteappfirebase.core.service

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthServiceImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthService {
    override suspend fun loginWithGoogle(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val idToken = getGoogleCredential(context)
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                authResult.user != null
            } catch (e: GetCredentialException) {
                Log.e("AuthService", "Google login failed", e)
                false
            } catch (e: Exception) {
                Log.e("AuthService", "Something went wrong", e)
                false
            }
        }
    }

    override fun logout() = firebaseAuth.signOut()
    override fun getLoggedInUser() = firebaseAuth.currentUser
    override fun getUid() = firebaseAuth.uid

    private suspend fun getGoogleCredential(context: Context): String? {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("73875623256-63nkcd0kkvj4v69ngafc5gk3r3ime1ac.apps.googleusercontent.com")
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            return result.credential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN")
        } catch (e: Exception) {
            Log.e("AuthService", "Error fetching credential", e)
            null
        }
    }
}