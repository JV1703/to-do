package com.example.to_dolistclone.core.data.local.model.relations.one_to_many

import androidx.room.Embedded
import androidx.room.Relation
import com.example.to_dolistclone.core.data.local.model.*
import com.example.to_dolistclone.core.domain.model.TodoDetails

data class TodoDetailsEntity(
    @Embedded val todo: TodoEntity,
    @Relation(
        parentColumn = "todoId",
        entityColumn = "todoRefId"
    )
    val tasks: List<TaskEntity>,
    @Relation(
        parentColumn = "todoId",
        entityColumn = "noteId"
    )
    val note: NoteEntity?,
    @Relation(
        parentColumn = "todoId",
        entityColumn = "todoRefId"
    )
    val attachments: List<AttachmentEntity>
)

fun TodoDetailsEntity.toTodoDetails() = TodoDetails(
    todo = todo.toTodo(),
    tasks = tasks.map { it.toTask() }.sortedBy { it.position },
    note = note?.let { it.toNote() },
    attachments = attachments.map { it.toAttachment() }
)