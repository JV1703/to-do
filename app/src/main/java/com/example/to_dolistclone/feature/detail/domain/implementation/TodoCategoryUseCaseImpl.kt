package com.example.to_dolistclone.feature.detail.domain.implementation

import com.example.to_dolistclone.core.domain.model.TodoCategory
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.TodoCategoryUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoCategoryUseCaseImpl @Inject constructor(private val todoRepository: TodoRepository): TodoCategoryUseCase {

    override suspend fun insertTodoCategory(category: TodoCategory): Long = todoRepository.insertTodoCategory(category)

    override fun getTodoCategories(): Flow<List<TodoCategory>> = todoRepository.getTodoCategories()

    override suspend fun deleteTodoCategory(todoCategoryName: String): Int = todoRepository.deleteTodoCategory(todoCategoryName)

}