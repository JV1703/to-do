package com.example.to_dolistclone.feature.detail.domain.implementation

import com.example.to_dolistclone.core.common.worker.WorkerManager
import com.example.to_dolistclone.core.data.local.GenericCacheError.GENERIC_CACHE_ERROR
import com.example.to_dolistclone.core.data.local.handleCacheResponse
import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.Task
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.DetailTaskUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DetailTaskUseCaseImpl @Inject constructor(
    private val todoRepository: TodoRepository, private val workerManager: WorkerManager
) : DetailTaskUseCase {

    override suspend fun insertTask(userId: String, task: Task): Async<Long?> {
        val cacheResult = todoRepository.insertTask(task)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTask(userId = userId, taskId = task.taskId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun updateTaskPosition(userId: String, taskId: String, position: Int): Async<Int> {
        val cacheResult = todoRepository.updateTaskPosition(taskId, position)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTask(
                    userId = userId,
                    taskId = taskId
                )
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun updateTaskPositionNetwork(
        userId: String, todoId: String
    ) {
        val cacheResult = todoRepository.getTodoWithTasks(todoId).first()
        cacheResult?.let { todoWithTasks ->
            if (todoWithTasks.tasks.isNotEmpty()) {
                todoWithTasks.tasks.forEach { task ->
                    workerManager.upsertTask(
                        userId = userId,
                        taskId = task.taskId
                    )
                }
            }
        }
    }

    override suspend fun updateTaskTitle(
        userId: String, taskId: String, title: String
    ): Async<Int> {
        val cacheResult = todoRepository.updateTaskTitle(taskId = taskId, title = title)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTask(userId = userId, taskId = taskId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun updateTaskCompletion(
        userId: String, taskId: String, isComplete: Boolean
    ): Async<Int> {
        val cacheResult = todoRepository.updateTaskCompletion(
            taskId = taskId, isComplete = isComplete
        )
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTask(
                    userId = userId,
                    taskId = taskId
                )
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun deleteTask(userId: String, taskId: String): Async<Int> {
        val cacheResult = todoRepository.deleteTask(taskId)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.deleteTask(userId = userId, taskId = taskId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }
}