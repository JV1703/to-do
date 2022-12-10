package com.example.to_dolistclone.feature.todo_shortcut.domain.implementation

import android.util.Log
import com.example.to_dolistclone.core.common.worker.WorkerManager
import com.example.to_dolistclone.core.data.local.GenericCacheError.GENERIC_CACHE_ERROR
import com.example.to_dolistclone.core.data.local.handleCacheResponse
import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.Task
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.domain.model.TodoCategory
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import javax.inject.Inject

class TodoShortcutUseCaseImpl @Inject constructor(
    private val todoRepository: TodoRepository, private val workerManager: WorkerManager
) {

    suspend fun insertTodo(userId: String, todo: Todo): Async<Long> {
        val cacheResult = todoRepository.insertTodo(todo)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTodo(userId, todo.todoId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    suspend fun insertTask(userId: String, task: Task): Async<Long> {
        Log.i("insertTask", "triggered")
        val cacheResult = todoRepository.insertTask(task)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTask(userId, task.taskId)
                Log.i("insertTask", "success")
                Async.Success(resultObj)
            } else {
                Log.i("insertTask", "fail")
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    suspend fun insertTasks(userId: String, tasks: List<Task>): LongArray {
        val tasksWithNonEmptyTitle = tasks.filter {
            it.task.trim().isNotEmpty()
        }.mapIndexed { index, task ->
            task.copy(position = index)
        }

        val cache = todoRepository.insertTasks(tasksWithNonEmptyTitle)

        Log.i("insertTasks", "filtered: $tasksWithNonEmptyTitle, todoRefId: ${tasksWithNonEmptyTitle.find { it.todoRefId != null }?.todoRefId}" )
        tasksWithNonEmptyTitle.find { it.todoRefId != null }?.let {
            workerManager.upsertTasks(userId, tasksWithNonEmptyTitle[0].todoRefId!!)
        }
        return cache
    }

    suspend fun insertTodoCategory(userId: String, todoCategoryName: String): Async<Long> {
        val todoCategory = TodoCategory(todoCategoryName)
        val cacheResult = todoRepository.insertTodoCategory(todoCategory)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.upsertTodoCategory(userId, todoCategory.todoCategoryName)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    fun getTodoCategories() = todoRepository.getTodoCategories()

}