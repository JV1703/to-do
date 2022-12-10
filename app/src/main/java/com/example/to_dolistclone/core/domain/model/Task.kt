package com.example.to_dolistclone.core.domain.model

import com.example.to_dolistclone.core.data.local.model.TaskEntity
import com.example.to_dolistclone.core.data.remote.model.TaskNetwork
import com.example.to_dolistclone.feature.home.adapter.TaskProxy

data class Task(
    val taskId: String,
    val task: String,
    val isComplete: Boolean = false,
    val position: Int,
    val todoRefId: String?
)

fun Task.toTaskEntity() = TaskEntity(
    taskId = taskId,
    task = task,
    isComplete = isComplete,
    position = position,
    todoRefId = todoRefId
)

fun Task.toTaskProxy() = TaskProxy(
    taskId = taskId,
    task = task,
    isComplete = isComplete,
    position = position,
    todoRefId = todoRefId
)

fun Task.toTaskNetwork() = TaskNetwork(
    taskId = taskId,
    task = task,
    isComplete = isComplete,
    position = position,
    todoRefId = todoRefId
)