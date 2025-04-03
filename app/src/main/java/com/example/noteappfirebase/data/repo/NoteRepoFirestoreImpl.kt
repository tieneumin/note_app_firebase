package com.example.noteappfirebase.data.repo

import com.example.noteappfirebase.data.model.Note
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NoteRepoFirestoreImpl(
    private val db: FirebaseFirestore = Firebase.firestore
) : NoteRepo {
    private fun getCollectionRef(): CollectionReference {
        return db.collection("notes")
    }

    override fun getNotes() = callbackFlow {
        val listener = getCollectionRef().addSnapshotListener { value, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val notes = mutableListOf<Note>()
            value?.documents?.forEach { doc ->
                doc.toObject(Note::class.java)?.let { note ->
                    notes.add(note.copy(id = doc.id))
                }
            }
            trySend(notes)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addNote(note: Note) {
        val docRef = getCollectionRef().document()
        docRef.set(note.copy(id = docRef.id)).await()
    }

    override suspend fun getNoteById(id: String): Note? {
        val task =  getCollectionRef().document(id).get().await()
        return task.toObject(Note::class.java)
    }

    override suspend fun deleteNote(id: String) {
        getCollectionRef().document(id).delete().await()
    }

    override suspend fun updateNote(note: Note){
       getCollectionRef().document(note.id!!).set(note.copy()).await()
    }


}