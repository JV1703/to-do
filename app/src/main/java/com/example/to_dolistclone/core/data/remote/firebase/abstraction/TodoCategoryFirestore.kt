package com.example.to_dolistclone.core.data.remote.firebase.abstraction

import com.example.to_dolistclone.core.data.remote.model.TodoCategoryNetwork

interface TodoCategoryFirestore {
    suspend fun upsertTodoCategory(userId: String, todoCategory: TodoCategoryNetwork)

    suspend fun getTodoCategory(userId: String, attachmentId: String): TodoCategoryNetwork?

    suspend fun getAttachments(userId: String): List<TodoCategoryNetwork>

    suspend fun deleteAttachment(userId: String, attachmentId: String)
}