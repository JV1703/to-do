package com.example.to_dolistclone.core.data.remote.firebase.abstraction

import com.example.to_dolistclone.core.data.remote.model.TodoCategoryNetwork

interface TodoCategoryFirestore {
    suspend fun upsertTodoCategory(userId: String, todoCategory: TodoCategoryNetwork)

    suspend fun getTodoCategory(userId: String, todoCategoryName: String): TodoCategoryNetwork?

    suspend fun getTodoCategories(userId: String): List<TodoCategoryNetwork>

    suspend fun deleteTodoCategory(userId: String, todoCategoryName: String)
    suspend fun updateTodoCategory(userId: String, todoCategoryName: String, field: Map<String, Any>)
}