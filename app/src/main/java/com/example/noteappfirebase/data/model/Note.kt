package com.example.noteappfirebase.data.model

data class Note(
    val id: String? = null,
    val title: String = "",
    val desc: String = "",
    val color: Int = -1,
)