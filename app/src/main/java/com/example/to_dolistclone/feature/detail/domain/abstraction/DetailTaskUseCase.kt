package com.example.to_dolistclone.feature.detail.domain.abstraction

import com.example.to_dolistclone.core.domain.model.Task

interface DetailTaskUseCase {

    suspend fun insertTasks(tasks: List<Task>): LongArray
    suspend fun insertTask(task: Task): Long
    suspend fun updateTaskTitle(taskId: String, title: String): Int
    suspend fun updateTaskPosition(taskId: String, position: Int): Int
    suspend fun updateTaskCompletion(taskId: String, isComplete: Boolean): Int
    suspend fun deleteTask(taskId: String): Int

}