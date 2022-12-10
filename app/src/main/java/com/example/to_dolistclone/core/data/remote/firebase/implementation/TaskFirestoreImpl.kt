package com.example.to_dolistclone.core.data.remote.firebase.implementation

import com.example.to_dolistclone.core.data.remote.firebase.abstraction.TaskFirestore
import com.example.to_dolistclone.core.data.remote.model.TaskNetwork
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

val TASK_COLLECTION = "Tasks"

class TaskFirestoreImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    TaskFirestore {

    override suspend fun upsertTask(userId: String, task: TaskNetwork) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TASK_COLLECTION)
            .document(task.taskId)
            .set(task)
            .await()
    }

    override suspend fun getTasks(userId: String): List<TaskNetwork> {
        return firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TASK_COLLECTION)
            .get()
            .await()
            .toObjects(TaskNetwork::class.java)
    }

    override suspend fun getTasks(userId: String, todoRefId: String): List<TaskNetwork> {
        return firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TASK_COLLECTION)
            .whereEqualTo("todoRefId", todoRefId)
            .get()
            .await()
            .toObjects(TaskNetwork::class.java)
    }

    override suspend fun getTask(userId: String, taskId: String): TaskNetwork?{
        return firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TASK_COLLECTION)
            .document(taskId)
            .get()
            .await()
            .toObject(TaskNetwork::class.java)
    }

    override suspend fun deleteTask(userId: String, taskId: String) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TASK_COLLECTION)
            .document(taskId)
            .delete()
            .await()
    }

    override suspend fun deleteTasks(userId: String, todoRefId: String) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TASK_COLLECTION)
            .whereEqualTo("todoRefId", todoRefId)
            .get()
            .addOnSuccessListener { tasks ->
                tasks.forEach { task ->
                    task.reference.delete()
                }
            }
            .await()
    }

    override suspend fun updateTaskPosition(userId: String, taskId: String, position: Int) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TASK_COLLECTION)
            .document(taskId)
            .update("position", position)
            .await()
    }

    override suspend fun updateTaskTitle(userId: String, taskId: String, title: String) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TASK_COLLECTION)
            .document(taskId)
            .update("task", title)
            .await()
    }

    override suspend fun updateTaskCompletion(userId: String, taskId: String, isComplete: Boolean) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TASK_COLLECTION)
            .document(taskId)
            .update("isComplete", isComplete)
            .await()
    }

    override suspend fun updateTask(userId: String, taskId: String, field: Map<String, Any>) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(TASK_COLLECTION)
            .document(taskId)
            .update(field)
            .await()
    }

}