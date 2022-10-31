package com.example.to_dolistclone.core.domain.model

import com.example.to_dolistclone.core.data.local.model.TaskEntity

data class Task(
    val taskId: Int,
    val task: String,
    val isComplete: Boolean = false,
    val todoRefId: String
)

fun Task.toTaskEntity() = TaskEntity(
    taskId = taskId, task = task, isComplete = isComplete, todoRefId = todoRefId
)