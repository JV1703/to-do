package com.example.to_dolistclone.core.domain.model.relation.one_to_one

import com.example.to_dolistclone.core.data.local.model.NoteEntity
import com.example.to_dolistclone.core.data.local.model.TodoEntity

data class TodoAndNote(
    val todo: TodoEntity,
    val note: NoteEntity?
)