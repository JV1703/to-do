package com.example.to_dolistclone.core.data.remote.abstraction

import com.example.to_dolistclone.core.data.remote.ApiResult
import com.example.to_dolistclone.core.data.remote.model.*

interface RemoteDataSource {
    suspend fun upsertTodo(userId: String, todo: TodoNetwork): ApiResult<Unit?>

    suspend fun getTodos(userId: String): ApiResult<List<TodoNetwork>?>

    suspend fun upsertTodoCategory(
        userId: String,
        todoCategory: TodoCategoryNetwork
    ): ApiResult<Unit?>

    suspend fun insertTask(userId: String, task: TaskNetwork): ApiResult<Unit?>
    suspend fun updateTodo(
        userId: String,
        todoId: String,
        field: Map<String, Any?>
    ): ApiResult<Unit?>

    suspend fun deleteTodo(userId: String, todoId: String): ApiResult<Unit?>
    suspend fun getTasks(userId: String): ApiResult<List<TaskNetwork>?>
    suspend fun deleteTodoCategory(userId: String, todoCategoryName: String): ApiResult<Unit?>
    suspend fun updateTodoCategory(
        userId: String,
        todoCategoryName: String,
        field: Map<String, Any>
    ): ApiResult<Unit?>

    suspend fun getTodoCategories(userId: String): ApiResult<List<TodoCategoryNetwork>?>
    suspend fun updateTask(
        userId: String,
        taskId: String,
        field: Map<String, Any>
    ): ApiResult<Unit?>

    suspend fun deleteTask(userId: String, taskId: String): ApiResult<Unit?>
    suspend fun insertNote(userId: String, note: NoteNetwork): ApiResult<Unit?>
    suspend fun deleteNote(userId: String, noteId: String): ApiResult<Unit?>
    suspend fun insertAttachment(userId: String, attachment: AttachmentNetwork): ApiResult<Unit?>
    suspend fun deleteAttachment(userId: String, attachmentId: String): ApiResult<Unit?>
}