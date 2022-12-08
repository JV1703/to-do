package com.example.to_dolistclone.core.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.to_dolistclone.core.domain.model.Note

@Entity(
    tableName = "note",
    foreignKeys = [ForeignKey(
        entity = TodoEntity::class,
        parentColumns = arrayOf("todoId"),
        childColumns = arrayOf("noteId"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = false)
    val noteId: String,
    val title: String,
    val body: String,
    val created_at: Long,
    val updated_at: Long
)

fun NoteEntity.toNote() = Note(
    noteId = noteId,
    body = body,
    title = title,
    created_at = created_at,
    updated_at = updated_at
)