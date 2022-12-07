package com.example.to_dolistclone.core.data.remote.firebase

import android.util.Log
import com.example.to_dolistclone.core.data.remote.model.TodoNetwork
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

val ACTIVE_COLLECTION = "Active"
val TEST_USER_ID_DOCUMENT = "5H0SmyClFgZqvE8bF55HofmTECm1"

class TodoFirestoreImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    val TODO_COLLECTION = "Todos"

    suspend fun upsertTodo(todo: TodoNetwork) {
        try {
            firestore
                .collection(ACTIVE_COLLECTION)
                .document(TEST_USER_ID_DOCUMENT)
                .collection(TODO_COLLECTION)
                .document(todo.todoId)
                .set(todo)
                .await()
        } catch (e: Exception) {
            Log.e("TodoFirestoreImpl", "upsertTodo - errorMsg: ${e.message}")
        }
    }

    suspend fun getTodos(userId: String): List<TodoNetwork> {
        return firestore.collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TODO_COLLECTION)
            .get()
            .await()
            .toObjects(TodoNetwork::class.java)
            .sortedWith(compareBy({ it.completedOn }, { it.todoId }))
    }

    suspend fun deleteTodo(userId: String, todoId: String) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TODO_COLLECTION)
            .document(todoId)
            .delete()
            .await()
    }

    suspend fun updateTodoTitle(userId: String, todoId: String, title: String) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TODO_COLLECTION)
            .document(todoId)
            .update("title", title)
            .await()
    }

    suspend fun updateTodoCategory(userId: String, todoId: String, todoCategoryRefName: String) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TODO_COLLECTION)
            .document(todoId)
            .update("todoCategoryRefName", todoCategoryRefName)
            .await()
    }

    suspend fun updateTodoDeadline(userId: String, todoId: String, deadline: Long?) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TODO_COLLECTION)
            .document(todoId)
            .update("deadline", deadline)
            .await()
    }

    suspend fun updateTodoReminder(userId: String, todoId: String, reminder: Long?) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TODO_COLLECTION)
            .document(todoId)
            .update("reminder", reminder)
            .await()
    }

    suspend fun updateTodoCompletion(userId: String, todoId: String, isComplete: Boolean, completedOn: Long?) {
        val dataToUpdate = mapOf("isComplete" to isComplete, "completedOn" to completedOn)
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TODO_COLLECTION)
            .document(todoId)
            .update(dataToUpdate)
            .await()
    }

    suspend fun updateTodoTasksAvailability(userId: String, todoId: String, tasksAvailability: Boolean) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TODO_COLLECTION)
            .document(todoId)
            .update("tasks", tasksAvailability)
            .await()
    }

    suspend fun updateTodoNotesAvailability(userId: String, todoId: String, notesAvailability: Boolean) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TODO_COLLECTION)
            .document(todoId)
            .update("notes", notesAvailability)
            .await()
    }

    suspend fun updateTodoAttachmentsAvailability(
        userId: String, todoId: String,
        attachmentsAvailability: Boolean
    ) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TODO_COLLECTION)
            .document(todoId)
            .update("attachments", attachmentsAvailability)
            .await()
    }

    suspend fun updateTodoAlarmRef(userId: String, todoId: String, alarmRef: Int?) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TODO_COLLECTION)
            .document(todoId)
            .update("alarmRef", alarmRef)
            .await()
    }

}