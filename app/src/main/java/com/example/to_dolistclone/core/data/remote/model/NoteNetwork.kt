package com.example.to_dolistclone.core.data.remote.model

import com.example.to_dolistclone.core.data.local.model.NoteEntity

data class NoteNetwork(
    val noteId: String = "",
    val title: String = "",
    val body: String = "",
    val created_at: Long = 0,
    val updated_at: Long = 0
)

fun NoteNetwork.toNoteEntity() = NoteEntity(
    noteId = noteId,
    title = title,
    body = body,
    created_at = created_at,
    updated_at = updated_at
)