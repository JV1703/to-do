package com.example.to_dolistclone.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.to_dolistclone.core.domain.model.Note

@Entity(tableName = "note")
data class NoteEntity(
    @PrimaryKey(autoGenerate = false)
    val noteId: String,
    val title: String,
    val body: String,
    val created_at: Long,
    val updated_at: Long
)

fun NoteEntity.toNote() = Note(
    noteId = this.noteId,
    body = this.body,
    title = this.title,
    created_at = this.created_at,
    updated_at = this.updated_at
)