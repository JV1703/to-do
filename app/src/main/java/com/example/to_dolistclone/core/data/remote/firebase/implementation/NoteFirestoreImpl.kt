package com.example.to_dolistclone.core.data.remote.firebase.implementation

import com.example.to_dolistclone.core.data.remote.firebase.abstraction.NoteFirestore
import com.example.to_dolistclone.core.data.remote.model.NoteNetwork
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

val NOTE_COLLECTION = "Notes"

class NoteFirestoreImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    NoteFirestore {

    override suspend fun upsertNote(userId: String, note: NoteNetwork) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(NOTE_COLLECTION)
            .document(note.noteId)
            .set(note)
            .await()
    }

    override suspend fun getNote(userId: String, noteId: String): NoteNetwork? {
        return firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(NOTE_COLLECTION)
            .document(noteId)
            .get()
            .await()
            .toObject(NoteNetwork::class.java)
    }

    override suspend fun getNotes(userId: String): List<NoteNetwork> {
        return firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(NOTE_COLLECTION)
            .get()
            .await()
            .toObjects(NoteNetwork::class.java)
    }

    override suspend fun deleteNote(userId: String, noteId: String) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(NOTE_COLLECTION)
            .document(noteId)
            .delete()
            .await()
    }

}