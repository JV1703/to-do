package com.example.to_dolistclone.core.data.remote.firebase.abstraction

import com.example.to_dolistclone.core.data.remote.model.TaskNetwork

interface TaskFirestore {
    suspend fun upsertTask(userId: String, task: TaskNetwork)

    suspend fun getTasks(userId: String): List<TaskNetwork>

    suspend fun getTasks(userId: String, todoRefId: String): List<TaskNetwork>

    suspend fun deleteTask(userId: String, taskId: String)

    suspend fun deleteTasks(userId: String, todoRefId: String)

    suspend fun updateTask(userId: String, taskId: String, field: Map<String, Any>)

    suspend fun getTask(userId: String, taskId: String): TaskNetwork?
}