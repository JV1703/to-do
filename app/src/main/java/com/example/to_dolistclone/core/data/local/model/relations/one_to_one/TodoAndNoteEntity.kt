package com.example.to_dolistclone.core.data.local.model.relations.one_to_one

import androidx.room.Embedded
import androidx.room.Relation
import com.example.to_dolistclone.core.data.local.model.NoteEntity
import com.example.to_dolistclone.core.data.local.model.TodoEntity
import com.example.to_dolistclone.core.data.local.model.toNote
import com.example.to_dolistclone.core.domain.model.relation.one_to_one.TodoAndNote

data class TodoAndNoteEntity(
    @Embedded val todo: TodoEntity,
    @Relation(
        parentColumn = "todoId",
        entityColumn = "noteId"
    )
    val note: NoteEntity
)

fun TodoAndNoteEntity.toNote() = this.note.toNote()

fun TodoAndNoteEntity.toTodoAndNote() = TodoAndNote(
    todo = todo, note = note
)