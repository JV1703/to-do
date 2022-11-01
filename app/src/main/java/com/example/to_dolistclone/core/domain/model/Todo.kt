package com.example.to_dolistclone.core.domain.model

import com.example.to_dolistclone.core.data.local.model.TodoEntity

data class Todo(
    val todoId: String,
    val title: String,
    val deadline: Long?,
    val reminder: Boolean = false,
    val repeat: String? = null,
    val isComplete: Boolean = false,
    val createdOn: Long?,
    val completedOn: Long?,
    val tasks: Boolean = false,
    val note: Boolean = false,
    val attachments: Boolean = false,
    val todoCategoryRefName: String
)

fun Todo.toTodoEntity(): TodoEntity = TodoEntity(
    todoId = todoId,
    title = title,
    deadline = deadline,
    reminder = reminder,
    repeat = repeat,
    isComplete = isComplete,
    createdOn = createdOn,
    completedOn = completedOn,
    tasks = tasks,
    note = note,
    attachments = attachments,
    todoCategoryRefName = todoCategoryRefName
)