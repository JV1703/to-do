package com.example.to_dolistclone.core.domain.model.relation.one_to_one

import com.example.to_dolistclone.core.domain.model.Note
import com.example.to_dolistclone.core.domain.model.Todo

data class TodoAndNote(
    val todo: Todo,
    val note: Note?
)