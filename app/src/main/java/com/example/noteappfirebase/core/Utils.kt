package com.example.noteappfirebase.core

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Toast
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

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun showErrorSnackbar(view: View, message: String, context: Context) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).apply {
        setBackgroundTint(ContextCompat.getColor(context, R.color.red))
        setTextColor(Color.WHITE)
    }.show()
}