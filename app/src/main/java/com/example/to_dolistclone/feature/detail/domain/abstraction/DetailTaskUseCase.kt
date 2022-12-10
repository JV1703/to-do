package com.example.to_dolistclone.feature.detail.domain.abstraction

import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.Task

interface DetailTaskUseCase {

    suspend fun insertTask(userId: String, task: Task, todoUpdatedOn: Long): Async<Long?>
    suspend fun updateTaskTitle(
        userId: String, taskId: String, title: String, todoId: String, todoUpdatedOn: Long
    ): Async<Int>

    suspend fun updateTaskPosition(
        userId: String, taskId: String, position: Int, todoId: String, todoUpdatedOn: Long
    ): Async<Int>

    suspend fun updateTaskPositionNetwork(userId: String, todoId: String, todoUpdatedOn: Long)
    suspend fun updateTaskCompletion(
        userId: String, taskId: String, isComplete: Boolean, todoId: String, todoUpdatedOn: Long
    ): Async<Int>

    suspend fun deleteTask(userId: String, taskId: String, todoId: String, todoUpdatedOn: Long): Async<Int>
}