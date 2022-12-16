package com.example.to_dolistclone.feature.home.domain.implementation

import com.example.to_dolistclone.core.data.local.handleCacheResponse
import com.example.to_dolistclone.core.data.remote.handleApiResponse
import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.TodoCategory
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.home.domain.abstraction.HomeTodoCategoryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeTodoCategoryUseCaseImpl @Inject constructor(private val todoRepository: TodoRepository) :
    HomeTodoCategoryUseCase {

    private val todoCategories = todoRepository.getTodoCategories()

    override fun getTodoCategoriesName(): Flow<List<String>> =
        todoCategories.map { it.map { it.todoCategoryName } }

    override suspend fun insertTodoCategory(userId: String, todoCategoryName: String): Long = todoRepository.insertTodoCategory(TodoCategory(todoCategoryName))
}