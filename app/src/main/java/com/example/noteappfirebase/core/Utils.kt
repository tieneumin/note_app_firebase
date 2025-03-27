package com.example.noteappfirebase.core

import android.util.Log

fun log(value: String, tag: String = "debugging") {
    Log.d(tag, value)
}

fun String.crop(charCount: Int): String {
    if (this.length <= charCount) {
        return this
    }
    return this.take(charCount) + "..."
}