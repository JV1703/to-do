package com.example.to_dolistclone.core.data.remote.firebase.implementation

import com.example.to_dolistclone.core.data.remote.model.TodoCategoryNetwork
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

val TODO_CATEGORY_COLLECTION = "Todo Categories"

class TodoCategoryFirestoreImpl @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun upsertTodoCategory(userId: String, todoCategory: TodoCategoryNetwork) {
        firestore.collection(ACTIVE_COLLECTION).document(userId)
            .collection(TODO_CATEGORY_COLLECTION).document(todoCategory.todoCategoryName)
            .set(todoCategory).await()
    }

    suspend fun getTodoCategory(userId: String, attachmentId: String): TodoCategoryNetwork? {
        return firestore.collection(ACTIVE_COLLECTION).document(userId)
            .collection(TODO_CATEGORY_COLLECTION).document(attachmentId).get().await()
            .toObject(TodoCategoryNetwork::class.java)
    }

    suspend fun getAttachments(userId: String): List<TodoCategoryNetwork> {
        return firestore.collection(ACTIVE_COLLECTION).document(userId)
            .collection(TODO_CATEGORY_COLLECTION).get().await()
            .toObjects(TodoCategoryNetwork::class.java)
    }

    suspend fun deleteAttachment(userId: String, attachmentId: String) {
        firestore.collection(ACTIVE_COLLECTION).document(userId)
            .collection(TODO_CATEGORY_COLLECTION).document(attachmentId).delete().await()
    }

}