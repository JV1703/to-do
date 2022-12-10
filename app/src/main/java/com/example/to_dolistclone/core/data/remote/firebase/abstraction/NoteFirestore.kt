package com.example.to_dolistclone.core.data.remote.firebase.abstraction

import com.example.to_dolistclone.core.data.remote.model.NoteNetwork

interface NoteFirestore {
    suspend fun upsertNote(userId: String, note: NoteNetwork)

    suspend fun getNote(userId: String, noteId: String): NoteNetwork?

    suspend fun getNotes(userId: String): List<NoteNetwork>

    suspend fun deleteNote(userId: String, noteId: String)
    suspend fun updateNote(userId: String, noteId: String, field: Map<String, Any>)
}