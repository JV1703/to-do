package com.example.to_dolistclone.core.data.remote.implementation

import com.example.to_dolistclone.core.data.remote.ApiResult
import com.example.to_dolistclone.core.data.remote.abstraction.RemoteDataSource
import com.example.to_dolistclone.core.data.remote.firebase.abstraction.*
import com.example.to_dolistclone.core.data.remote.model.TodoCategoryNetwork
import com.example.to_dolistclone.core.data.remote.model.TodoNetwork
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

    override suspend fun insertTodo(userId: String, todo: TodoNetwork) = safeApiCall(dispatcherIO) {
        todoFs.insertTodo(userId, todo)
    }

    override suspend fun upsertTodoCategory(userId: String, todoCategory: TodoCategoryNetwork) = safeApiCall(dispatcherIO){
        todoCategoryFs.upsertTodoCategory(userId, todoCategory)
    }

    override suspend fun getTodos(userId: String): ApiResult<List<TodoNetwork>?> {
        return safeApiCall(dispatcherIO) {
            todoFs.getTodos(userId)
        }
    }

}