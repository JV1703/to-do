package com.example.to_dolistclone.core.domain.model

data class TodoDetails(
    val todo: Todo,
    val tasks: List<Task>,
    val note: Note?,
    val attachments: List<Attachment>
)