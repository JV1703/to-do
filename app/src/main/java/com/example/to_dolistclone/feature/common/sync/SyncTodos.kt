package com.example.to_dolistclone.feature.common.sync

import com.example.to_dolistclone.core.data.remote.ApiResult
import com.example.to_dolistclone.core.data.remote.model.TodoNetwork
import com.example.to_dolistclone.core.domain.model.Note
import com.example.to_dolistclone.core.domain.model.Task
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncTodos @Inject constructor(
    private val todoRepository: TodoRepository, private val firebaseAuth: FirebaseAuth
) {

    private suspend fun getCachedTodos(): List<Todo> {
        return todoRepository.getTodos().first()
    }

    private suspend fun getAllTodoNetwork(userId: String): ApiResult<List<TodoNetwork>?> {
        return todoRepository.getTodosNetwork(userId)
    }

    private suspend fun checkIfCachedTodosRequiresUpdate(
        userId: String, cachedData: Todo, networkData: Todo
    ) {

        val cacheUpdatedOn = cachedData.updatedOn
        val networkUpdatedOn = networkData.updatedOn

        if (networkUpdatedOn > cacheUpdatedOn) {
            todoRepository.updateTodo(
                networkData.todoId,
                networkData.title,
                networkData.deadline,
                networkData.reminder,
                networkData.repeat,
                networkData.isComplete,
                networkData.createdOn,
                networkData.updatedOn,
                networkData.completedOn,
                networkData.tasks,
                networkData.notes,
                networkData.attachments,
                networkData.alarmRef,
                networkData.todoCategoryRefName
            )
        } else if (networkUpdatedOn < cacheUpdatedOn) {
            todoRepository.upsertTodoNetwork(userId, cachedData)
        }

    }

    private suspend fun updateCacheTodo(networkTodo: Todo) {
        todoRepository.updateTodo(
            networkTodo.todoId,
            networkTodo.title,
            networkTodo.deadline,
            networkTodo.reminder,
            networkTodo.repeat,
            networkTodo.isComplete,
            networkTodo.createdOn,
            networkTodo.updatedOn,
            networkTodo.completedOn,
            networkTodo.tasks,
            networkTodo.notes,
            networkTodo.attachments,
            networkTodo.alarmRef,
            networkTodo.todoCategoryRefName
        )
    }

    private suspend fun insertTodoToCache(networkTodo: Todo) {
        todoRepository.insertTodo(networkTodo)
    }

    private suspend fun updateNetworkTodo(userId: String, cacheTodo: Todo) {
        todoRepository.upsertTodoNetwork(userId, cacheTodo)
    }

    private suspend fun upsertCacheTasks(networkTasks: List<Task>) {
        todoRepository.insertTasks(networkTasks)
    }

    private suspend fun upsertNetworkTasks(userId: String, cacheTasks: List<Task>) {
        cacheTasks.forEach { task ->
            todoRepository.upsertTaskNetwork(userId, task)
        }
    }

    private suspend fun upsertCacheNote(note: Note) {
        todoRepository.insertNote(note)
    }

    private suspend fun upsertNetworkNote(userId: String, note: Note) {
        todoRepository.upsertNoteNetwork(userId, note)
    }

}