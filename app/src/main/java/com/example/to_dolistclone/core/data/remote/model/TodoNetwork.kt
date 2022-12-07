package com.example.to_dolistclone.core.data.remote.model

import com.example.to_dolistclone.core.data.local.model.TodoEntity
import com.google.firebase.firestore.PropertyName

data class TodoNetwork(
    val todoId: String = "",
    val title: String = "",
    val deadline: Long? = null,
    val reminder: Long? = null,
    val repeat: String? = null,
    @get:PropertyName("isComplete") //Firebase will automatically remove name with "get", "has" and "is"
    val isComplete: Boolean = false,
    val createdOn: Long? = null,
    val completedOn: Long? = null,
    val tasks: Boolean = false,
    val notes: Boolean = false,
    val attachments: Boolean = false,
    val alarmRef: Int? = null,
    val todoCategoryRefName: String = ""
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