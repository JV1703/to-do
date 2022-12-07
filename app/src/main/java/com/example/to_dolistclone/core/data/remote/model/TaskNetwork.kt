package com.example.to_dolistclone.core.data.remote.model

import com.example.to_dolistclone.core.data.local.model.TaskEntity
import com.google.firebase.firestore.PropertyName

data class TaskNetwork(
    val taskId: String = "",
    val task: String = "",
    @get:PropertyName("isComplete") //Firebase will automatically remove name with "get", "has" and "is"
    val isComplete: Boolean = false,
    val position: Int = 0,
    val todoRefId: String? = null
)

fun TaskEntity.toTaskEntity() = TaskEntity(
    taskId = taskId,
    task = task,
    isComplete = isComplete,
    position = position,
    todoRefId = todoRefId
)