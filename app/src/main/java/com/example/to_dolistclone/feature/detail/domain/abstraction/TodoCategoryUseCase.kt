package com.example.to_dolistclone.feature.detail.domain.abstraction

import com.example.to_dolistclone.core.domain.model.TodoCategory
import kotlinx.coroutines.flow.Flow

interface TodoCategoryUseCase {

    suspend fun insertTodoCategory(category: TodoCategory): Long
    fun getTodoCategories(): Flow<List<TodoCategory>>
    suspend fun deleteTodoCategory(todoCategoryName: String): Int

}