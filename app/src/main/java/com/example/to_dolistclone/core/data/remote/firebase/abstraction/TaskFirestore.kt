package com.example.to_dolistclone.core.data.remote.firebase.abstraction

import com.example.to_dolistclone.core.data.remote.model.TaskNetwork

interface TaskFirestore {
    suspend fun upsertTask(userId: String, task: TaskNetwork)

    suspend fun getTasks(userId: String): List<TaskNetwork>

    suspend fun getTasks(userId: String, todoRefId: String): List<TaskNetwork>

    suspend fun deleteTask(userId: String, taskId: String)

    suspend fun deleteTasks(userId: String, todoRefId: String)

    suspend fun updateTaskPosition(userId: String, taskId: String, position: Int)
    suspend fun updateTaskTitle(userId: String, taskId: String, title: String)
    suspend fun updateTaskCompletion(userId: String, taskId: String, isComplete: Boolean)
}