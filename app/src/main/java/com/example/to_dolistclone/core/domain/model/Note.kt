package com.example.to_dolistclone.core.domain.model

import com.example.to_dolistclone.core.data.local.model.NoteEntity

data class Note(
    val noteId: String,
    val body: String,
    val title: String,
    val created_at: Long,
    val updated_at: Long
)

fun Note.toNoteEntity() = NoteEntity(
    noteId = noteId, title = title, body = body, created_at = created_at, updated_at = updated_at
)