package com.example.to_dolistclone.feature.home.domain.implementation

import com.example.to_dolistclone.core.common.worker.WorkerManager
import com.example.to_dolistclone.core.data.local.GenericCacheError.GENERIC_CACHE_ERROR
import com.example.to_dolistclone.core.data.local.handleCacheResponse
import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.TodoCategory
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.home.domain.abstraction.HomeTodoCategoryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeTodoCategoryUseCaseImpl @Inject constructor(private val todoRepository: TodoRepository, private val workerManager: WorkerManager) :
    HomeTodoCategoryUseCase {

    private val todoCategories = todoRepository.getTodoCategories()

    override fun getTodoCategoriesName(): Flow<List<String>> =
        todoCategories.map { it.map { it.todoCategoryName } }

    override suspend fun insertTodoCategory(userId: String, todoCategoryName: String): Async<Long> {
        val todoCategory = TodoCategory(todoCategoryName)
        val cacheResult = todoRepository.insertTodoCategory(todoCategory)
        return handleCacheResponse(cacheResult) { resultObj ->
            if(resultObj > 0){
                workerManager.upsertTodoCategory(
                    userId = userId,
                    todoCategoryName = todoCategoryName
                )
                Async.Success(resultObj)
            }else{
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }
}