package com.example.to_dolistclone.core.data.remote.model

import com.example.to_dolistclone.core.data.local.model.TodoEntity

data class TodoNetwork(
    val todoId: String,
    val title: String,
    val deadline: Long?,
    val reminder: Long?,
    val repeat: String?,
    val isComplete: Boolean,
    val createdOn: Long?,
    val completedOn: Long?,
    val tasks: Boolean,
    val notes: Boolean,
    val attachments: Boolean,
    val alarmRef: Int?,
    val todoCategoryRefName: String
)

fun TodoNetwork.toTodoEntity() = TodoEntity(
    todoId = todoId,
    title = title,
    deadline = deadline,
    reminder = reminder,
    repeat = repeat,
    isComplete = isComplete,
    createdOn = createdOn,
    completedOn = completedOn,
    tasks = tasks,
    notes = notes,
    attachments = attachments,
    alarmRef = alarmRef,
    todoCategoryRefName = todoCategoryRefName
)