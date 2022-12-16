package com.example.to_dolistclone.core.data.remote.firebase.abstraction

import com.example.to_dolistclone.core.data.remote.model.TodoNetwork

interface TodoFirestore {

    suspend fun upsertTodo(userId: String, todo: TodoNetwork)

    suspend fun getTodos(userId: String): List<TodoNetwork>

    suspend fun deleteTodo(userId: String, todoId: String)

    suspend fun updateTodo(userId: String, todoId: String, field: Map<String, Any?>)
}