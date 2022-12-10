package com.example.to_dolistclone.core.data.remote.implementation

import com.example.to_dolistclone.core.data.remote.ApiResult
import com.example.to_dolistclone.core.data.remote.abstraction.RemoteDataSource
import com.example.to_dolistclone.core.data.remote.firebase.abstraction.*
import com.example.to_dolistclone.core.data.remote.model.*
import com.example.to_dolistclone.core.data.remote.safeApiCall
import com.example.to_dolistclone.core.di.coroutine_dispatchers.CoroutinesQualifiers
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val todoFs: TodoFirestore,
    private val taskFs: TaskFirestore,
    private val noteFs: NoteFirestore,
    private val attachmentFs: AttachmentFirestore,
    private val todoCategoryFs: TodoCategoryFirestore,
    @CoroutinesQualifiers.IoDispatcher private val dispatcherIO: CoroutineDispatcher
) : RemoteDataSource {

    override suspend fun upsertTodo(userId: String, todo: TodoNetwork) = safeApiCall(dispatcherIO) {
        todoFs.upsertTodo(userId, todo)
    }

    override suspend fun getTodos(userId: String): ApiResult<List<TodoNetwork>?> {
        return safeApiCall(dispatcherIO) {
            todoFs.getTodos(userId)
        }
    }

    override suspend fun updateTodo(userId: String, todoId: String, field: Map<String, Any?>) =
        safeApiCall(dispatcherIO) {
            todoFs.updateTodo(userId, todoId, field)
        }

    override suspend fun deleteTodo(userId: String, todoId: String) = safeApiCall(dispatcherIO) {
        todoFs.deleteTodo(userId, todoId)
    }

    override suspend fun upsertTodoCategory(userId: String, todoCategory: TodoCategoryNetwork) =
        safeApiCall(dispatcherIO) {
            todoCategoryFs.upsertTodoCategory(userId, todoCategory)
        }

    override suspend fun getTodoCategories(userId: String) = safeApiCall(dispatcherIO) {
        todoCategoryFs.getTodoCategories(userId)
    }

    override suspend fun updateTodoCategory(
        userId: String, todoCategoryName: String, field: Map<String, Any>
    ) = safeApiCall(dispatcherIO) {
        todoCategoryFs.updateTodoCategory(userId, todoCategoryName, field)
    }

    override suspend fun deleteTodoCategory(userId: String, todoCategoryName: String) =
        safeApiCall(dispatcherIO) {
            todoCategoryFs.deleteTodoCategory(userId, todoCategoryName)
        }

    override suspend fun insertTask(userId: String, task: TaskNetwork) = safeApiCall(dispatcherIO) {
        taskFs.upsertTask(userId, task)
    }

    override suspend fun getTasks(userId: String) = safeApiCall(dispatcherIO) {
        taskFs.getTasks(userId)
    }

    override suspend fun updateTask(userId: String, taskId: String, field: Map<String, Any>) =
        safeApiCall(dispatcherIO) {
            taskFs.updateTask(userId, taskId, field)
        }

    override suspend fun deleteTask(userId: String, taskId: String) = safeApiCall(dispatcherIO) {
        taskFs.deleteTask(userId, taskId)
    }

    override suspend fun insertNote(userId: String, note: NoteNetwork) = safeApiCall(dispatcherIO) {
        noteFs.upsertNote(userId = userId, note = note)
    }

    override suspend fun deleteNote(userId: String, noteId: String) = safeApiCall(dispatcherIO) {
        noteFs.deleteNote(userId, noteId)
    }

    override suspend fun insertAttachment(userId: String, attachment: AttachmentNetwork) =
        safeApiCall(dispatcherIO) {
            attachmentFs.upsertAttachment(userId = userId, attachment = attachment)
        }

    override suspend fun deleteAttachment(userId: String, attachmentId: String) =
        safeApiCall(dispatcherIO) {
            attachmentFs.deleteAttachment(userId = userId, attachmentId = attachmentId)
        }

}