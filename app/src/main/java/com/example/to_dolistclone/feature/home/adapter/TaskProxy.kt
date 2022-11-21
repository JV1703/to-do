package com.example.to_dolistclone.feature.home.adapter

import com.example.to_dolistclone.core.domain.model.Task
import java.util.*

data class TaskProxy(
    val taskId: String? = null,
    var task: String = "",
    var isComplete: Boolean = false,
    val position: Int,
    var todoRefId: String? = null
)

fun TaskProxy.toTask(todoRefId: String) = Task(
    taskId = taskId ?: UUID.randomUUID().toString(),
    task = task,
    isComplete = isComplete,
    position = position,
    todoRefId = todoRefId
)