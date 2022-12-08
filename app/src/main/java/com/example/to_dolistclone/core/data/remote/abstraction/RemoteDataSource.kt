package com.example.to_dolistclone.core.data.remote.abstraction

import com.example.to_dolistclone.core.data.remote.ApiResult
import com.example.to_dolistclone.core.data.remote.model.TodoCategoryNetwork
import com.example.to_dolistclone.core.data.remote.model.TodoNetwork

interface RemoteDataSource {
    suspend fun insertTodo(userId: String, todo: TodoNetwork): ApiResult<Unit?>

    suspend fun getTodos(userId: String): ApiResult<List<TodoNetwork>?>

    suspend fun upsertTodoCategory(
        userId: String,
        todoCategory: TodoCategoryNetwork
    ): ApiResult<Unit?>
}