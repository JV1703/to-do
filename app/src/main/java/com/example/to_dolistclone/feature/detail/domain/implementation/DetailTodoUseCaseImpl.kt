package com.example.to_dolistclone.feature.detail.domain.implementation

import android.util.Log
import com.example.to_dolistclone.core.common.worker.WorkerManager
import com.example.to_dolistclone.core.data.local.GenericCacheError.GENERIC_CACHE_ERROR
import com.example.to_dolistclone.core.data.local.handleCacheResponse
import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.TodoDetails
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.common.domain.todo.BaseTodoUseCaseImpl
import com.example.to_dolistclone.feature.detail.domain.abstraction.DetailTodoUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DetailTodoUseCaseImpl @Inject constructor(
    private val todoRepository: TodoRepository, private val workerManager: WorkerManager
) : BaseTodoUseCaseImpl(todoRepository), DetailTodoUseCase {

    override fun getTodoDetails(todoId: String): Flow<TodoDetails?> {
        Log.i("getTodoDetails", "triggered")
        return todoRepository.getTodoDetails(todoId)
    }

    override suspend fun updateTodoUpdatedOn(userId: String, todoId: String, updatedOn: Long): Async<Int> {
        val cacheResult = todoRepository.updateTodoUpdatedOn(todoId = todoId, updatedOn = updatedOn)
        return handleCacheResponse(cacheResult){resultObj ->
            if(resultObj > 0){
                workerManager.upsertTodo(userId, todoId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun updateTodoDeadline(
        userId: String, todoId: String, deadline: Long?, updatedOn: Long
    ): Async<Int> {
        val cacheResult = todoRepository.updateTodoDeadline(
            todoId = todoId,
            deadline = deadline,
            updatedOn = updatedOn
        )
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTodo(userId, todoId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }

    }

    override suspend fun updateTodoReminder(
        userId: String, todoId: String, reminder: Long?, updatedOn: Long
    ): Async<Int> {
        val cacheResult = todoRepository.updateTodoReminder(todoId, reminder, updatedOn)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTodo(userId, todoId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun updateTodoTitle(
        userId: String, todoId: String, title: String, updatedOn: Long
    ): Async<Int> {
        val cacheResult = todoRepository.updateTodoTitle(todoId, title, updatedOn)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTodo(userId, todoId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun updateTodoCategory(
        userId: String, todoId: String, category: String, updatedOn: Long
    ): Async<Int?> {
        val cacheResult = todoRepository.updateTodoCategory(todoId, category, updatedOn)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTodo(userId, todoId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun updateTodoCompletion(
        userId: String, todoId: String, isComplete: Boolean, completedOn: Long?, updatedOn: Long
    ): Async<Int> {
        val cacheResult = todoRepository.updateTodoCompletion(todoId, isComplete, completedOn, updatedOn)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTodo(userId, todoId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun updateTodoTasksAvailability(
        userId: String, todoId: String, tasksAvailability: Boolean, updatedOn: Long
    ): Async<Int> {
        val cacheResult = todoRepository.updateTodoTasksAvailability(todoId, tasksAvailability, updatedOn)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTodo(userId, todoId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun updateTodoNotesAvailability(
        userId: String, todoId: String, notesAvailability: Boolean, updatedOn: Long
    ): Async<Int> {
        val cacheResult = todoRepository.updateTodoNotesAvailability(todoId, notesAvailability, updatedOn)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTodo(userId, todoId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun updateTodoAttachmentsAvailability(
        userId: String, todoId: String, attachmentsAvailability: Boolean, updatedOn: Long
    ): Async<Int> {
        val cacheResult =
            todoRepository.updateTodoAttachmentsAvailability(todoId, attachmentsAvailability, updatedOn)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTodo(userId, todoId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun updateTodoAlarmRef(
        userId: String, todoId: String, alarmRef: Int?, updatedOn: Long
    ): Async<Int> {
        val cacheResult = todoRepository.updateTodoAlarmRef(todoId, alarmRef, updatedOn)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTodo(userId, todoId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun deleteTodo(userId: String, todoId: String): Async<Int> {
        val cacheResult = todoRepository.deleteTodo(todoId)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.deleteTodo(userId, todoId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

}