package com.example.to_dolistclone.core.data.remote.firebase.implementation

import android.util.Log
import com.example.to_dolistclone.core.data.remote.firebase.abstraction.TodoFirestore
import com.example.to_dolistclone.core.data.remote.model.TodoNetwork
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

val ACTIVE_COLLECTION = "Active"
val TEST_USER_ID_DOCUMENT = "5H0SmyClFgZqvE8bF55HofmTECm1"
val TODO_COLLECTION = "Todos"

class TodoFirestoreImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TodoFirestore {

    override suspend fun upsertTodo(userId: String, todo: TodoNetwork) {
        try {
            firestore
                .collection(ACTIVE_COLLECTION)
                .document(userId)
                .collection(TODO_COLLECTION)
                .document(todo.todoId)
                .set(todo)
                .await()
        } catch (e: Exception) {
            Log.e("TodoFirestoreImpl", "upsertTodo - errorMsg: ${e.message}")
        }
    }

    override suspend fun getTodos(userId: String): List<TodoNetwork> {
        return firestore.collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TODO_COLLECTION)
            .get()
            .await()
            .toObjects(TodoNetwork::class.java)
            .sortedWith(compareBy({ it.completedOn }, { it.todoId }))
    }

    override suspend fun deleteTodo(userId: String, todoId: String) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TODO_COLLECTION)
            .document(todoId)
            .delete()
            .await()
    }

    override suspend fun updateTodo(userId: String, todoId: String, field: Map<String, Any?>){
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TODO_COLLECTION)
            .document(todoId)
            .update(field)
            .await()
    }

}