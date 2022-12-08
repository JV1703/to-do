package com.example.to_dolistclone.feature.todo_shortcut.domain.implementation

import com.example.to_dolistclone.core.data.local.CacheResult
import com.example.to_dolistclone.core.data.local.handleCacheResponse
import com.example.to_dolistclone.core.data.remote.ApiResult
import com.example.to_dolistclone.core.data.remote.handleApiResponse
import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.Task
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.domain.model.TodoCategory
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import javax.inject.Inject

class TodoShortcutUseCaseImpl @Inject constructor(private val todoRepository: TodoRepository) {

    suspend fun insertTodo(todo: Todo) = todoRepository.insertTodo(todo)

    suspend fun insertTasks(tasks: List<Task>): LongArray {
        val filteredTasks = tasks.filter {
            it.task.trim().isNotEmpty()
        }.mapIndexed { index, task ->
            task.copy(position = index)
        }
        return todoRepository.insertTasks(filteredTasks)
    }

    suspend fun insertTodoCategory(userId: String, todoCategoryName: String): Async<Unit?> {
        val todoCategory = TodoCategory(todoCategoryName)
        val cacheResult = todoRepository.insertTodoCategory(todoCategory)
        return handleCacheResponse(cacheResult) { resultObj ->
            if(resultObj > 0){
                handleApiResponse(todoRepository.upsertTodoCategoryNetwork(userId, todoCategory))
            }else{
                Async.Error(errorMsg = "Caching failed")
            }
        }
    }

    fun getTodoCategories() = todoRepository.getTodoCategories()

}