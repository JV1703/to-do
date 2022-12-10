package com.example.to_dolistclone.feature.detail.domain.implementation

import com.example.to_dolistclone.core.common.worker.WorkerManager
import com.example.to_dolistclone.core.data.local.GenericCacheError.GENERIC_CACHE_ERROR
import com.example.to_dolistclone.core.data.local.handleCacheResponse
import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.Task
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.DetailTaskUseCase
import com.example.to_dolistclone.feature.detail.domain.abstraction.DetailTodoUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DetailTaskUseCaseImpl @Inject constructor(
    private val todoRepository: TodoRepository, private val detailTodoUseCase: DetailTodoUseCase, private val workerManager: WorkerManager
) : DetailTaskUseCase {

    override suspend fun insertTask(userId: String, task: Task, todoUpdatedOn: Long): Async<Long?> {
        val cacheResult = todoRepository.insertTask(task)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                detailTodoUseCase.updateTodoUpdatedOn(
                    userId = userId,
                    todoId = task.todoRefId!!,
                    updatedOn = todoUpdatedOn
                )
                workerManager.upsertTask(userId = userId, taskId = task.taskId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun updateTaskPosition(
        userId: String,
        taskId: String,
        position: Int,
        todoId: String,
        todoUpdatedOn: Long
    ): Async<Int> {
        val cacheResult = todoRepository.updateTaskPosition(taskId, position)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                detailTodoUseCase.updateTodoUpdatedOn(
                    userId = userId,
                    todoId = todoId,
                    updatedOn = todoUpdatedOn
                )
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
        userId: String,
        todoId: String,
        todoUpdatedOn: Long
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
        userId: String,
        taskId: String,
        title: String,
        todoId: String,
        todoUpdatedOn: Long
    ): Async<Int> {
        val cacheResult = todoRepository.updateTaskTitle(taskId = taskId, title = title)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                detailTodoUseCase.updateTodoUpdatedOn(
                    userId = userId,
                    todoId = todoId,
                    updatedOn = todoUpdatedOn
                )
                workerManager.upsertTask(userId = userId, taskId = taskId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun updateTaskCompletion(
        userId: String,
        taskId: String,
        isComplete: Boolean,
        todoId: String,
        todoUpdatedOn: Long
    ): Async<Int> {
        val cacheResult = todoRepository.updateTaskCompletion(
            taskId = taskId, isComplete = isComplete
        )
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                detailTodoUseCase.updateTodoUpdatedOn(
                    userId = userId,
                    todoId = todoId,
                    updatedOn = todoUpdatedOn
                )
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

    override suspend fun deleteTask(
        userId: String,
        taskId: String,
        todoId: String,
        todoUpdatedOn: Long
    ): Async<Int> {
        val cacheResult = todoRepository.deleteTask(taskId)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                detailTodoUseCase.updateTodoUpdatedOn(
                    userId = userId,
                    todoId = todoId,
                    updatedOn = todoUpdatedOn
                )
                workerManager.deleteTask(userId = userId, taskId = taskId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }
}