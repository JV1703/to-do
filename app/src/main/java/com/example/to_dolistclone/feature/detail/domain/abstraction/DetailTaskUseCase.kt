package com.example.to_dolistclone.feature.detail.domain.abstraction

import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.Task

interface DetailTaskUseCase {

    suspend fun insertTask(userId: String, task: Task): Async<Long?>
    suspend fun updateTaskTitle(userId: String, taskId: String, title: String): Async<Int>
    suspend fun updateTaskPosition(userId: String, taskId: String, position: Int): Async<Int>
    suspend fun updateTaskPositionNetwork(userId: String, todoId: String)
    suspend fun updateTaskCompletion(
        userId: String,
        taskId: String,
        isComplete: Boolean
    ): Async<Int>

    suspend fun deleteTask(userId: String, taskId: String): Async<Int>
}