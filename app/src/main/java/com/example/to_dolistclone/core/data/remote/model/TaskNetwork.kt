package com.example.to_dolistclone.core.data.remote.model

import com.example.to_dolistclone.core.data.local.model.TaskEntity

data class TaskNetwork(
    val taskId: String,
    val task: String,
    val isComplete: Boolean = false,
    val position: Int,
    val todoRefId: String?
)

fun TaskEntity.toTaskEntity() = TaskEntity(
    taskId = taskId,
    task = task,
    isComplete = isComplete,
    position = position,
    todoRefId = todoRefId
)