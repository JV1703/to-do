package com.example.to_dolistclone.feature.detail.domain.implementation

import com.example.to_dolistclone.core.common.worker.WorkerManager
import com.example.to_dolistclone.core.data.local.GenericCacheError.GENERIC_CACHE_ERROR
import com.example.to_dolistclone.core.data.local.handleCacheResponse
import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.TodoCategory
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.TodoCategoryUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoCategoryUseCaseImpl @Inject constructor(private val todoRepository: TodoRepository, private val workerManager: WorkerManager) :
    TodoCategoryUseCase {

    override suspend fun insertTodoCategory(userId: String, todoCategoryName: String) {
        val todoCategory = TodoCategory(todoCategoryName)
        val cacheResult = todoRepository.insertTodoCategory(todoCategory)
        handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTodoCategory(userId, todoCategoryName)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override fun getTodoCategories(): Flow<List<TodoCategory>> = todoRepository.getTodoCategories()

    override suspend fun deleteTodoCategory(todoCategoryName: String): Int =
        todoRepository.deleteTodoCategory(todoCategoryName)

}