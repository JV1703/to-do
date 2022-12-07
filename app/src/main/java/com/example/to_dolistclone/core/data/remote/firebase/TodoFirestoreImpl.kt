package com.example.to_dolistclone.core.data.remote.firebase

import android.util.Log
import com.example.to_dolistclone.core.data.remote.ApiResult
import com.example.to_dolistclone.core.data.remote.model.TodoNetwork
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

val ACTIVE_COLLECTION = "Active"
val USER_ID_DOCUMENT = "5H0SmyClFgZqvE8bF55HofmTECm1"

class TodoFirestoreImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    val TODO_COLLECTION = "Todos"

    suspend fun upsertTodo(todo: TodoNetwork): ApiResult<Unit> {
        return try {
            firestore
                .collection(ACTIVE_COLLECTION)
                .document(USER_ID_DOCUMENT)
                .collection(TODO_COLLECTION)
                .document(todo.todoId)
                .set(todo)
                .await()
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("TodoFirestoreImpl", "upsertTodo - errorMsg: ${e.message}")
            ApiResult.Error(e.message)
        }
    }

    suspend fun getTodos(): ApiResult<List<TodoNetwork>> {
        return try {
            val data = firestore.collection(ACTIVE_COLLECTION)
                .document(USER_ID_DOCUMENT)
                .collection(TODO_COLLECTION)
                .get()
                .await()
                .toObjects(TodoNetwork::class.java)

            ApiResult.Success(data)
        } catch (e: Exception) {
            Log.e("TodoFirestoreImpl", "getTodos - errorMsg: ${e.message}")
            ApiResult.Error(errorMsg = e.message)
        }
    }

    suspend fun deleteTodo(todoId: String): ApiResult<Unit> {
        return try {
            firestore
                .collection(ACTIVE_COLLECTION)
                .document(USER_ID_DOCUMENT)
                .collection(TODO_COLLECTION)
                .document(todoId)
                .delete()
                .await()

            ApiResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("TodoFirestoreImpl", "deleteTodo - errorMsg: ${e.message}")
            ApiResult.Error(e.message)
        }
    }

}