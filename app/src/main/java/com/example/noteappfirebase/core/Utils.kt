package com.example.noteappfirebase.core

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.noteappfirebase.R
import com.google.android.material.snackbar.Snackbar

fun log(value: String, tag: String = "debugging") {
    Log.d(tag, value)
}

fun String.crop(charCount: Int): String {
    if (this.length <= charCount) {
        return this
    }
    return this.take(charCount) + "..."
}

fun showError(view: View, context: Context, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).apply {
        setBackgroundTint(
            ContextCompat.getColor(context, R.color.red)
        )
    }.show()
}