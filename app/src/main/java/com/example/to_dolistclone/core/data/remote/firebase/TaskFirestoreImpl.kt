package com.example.to_dolistclone.core.data.remote.firebase

import android.util.Log
import com.example.to_dolistclone.core.data.remote.ApiResult
import com.example.to_dolistclone.core.data.remote.model.TaskNetwork
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TaskFirestoreImpl @Inject constructor(private val firestore: FirebaseFirestore) {

    val TASK_COLLECTION = "Task"

    suspend fun upsertTask(task: TaskNetwork):ApiResult<Unit> {
        return try {
            firestore
                .collection(ACTIVE_COLLECTION)
                .document(USER_ID_DOCUMENT)
                .collection(TASK_COLLECTION)
                .document(task.taskId)
                .set(task)
                .await()

            ApiResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("TaskFirestoreImpl", "upsertTask - errorMsg: ${e.message}")
            ApiResult.Error(e.message)
        }
    }

    suspend fun getTasks(): ApiResult<List<TaskNetwork>> {
        return try {
            val data = firestore
                .collection(ACTIVE_COLLECTION)
                .document(USER_ID_DOCUMENT)
                .collection(TASK_COLLECTION)
                .get()
                .await()
                .toObjects(TaskNetwork::class.java)

            ApiResult.Success(data)
        } catch (e: Exception) {
            Log.e("TaskFirestoreImpl", "getTasks() - errorMsg: ${e.message}")
            ApiResult.Error(e.message)
        }
    }

    suspend fun getTasks(todoRefId: String): ApiResult<List<TaskNetwork>>{
        return try {
            val data = firestore
                .collection(ACTIVE_COLLECTION)
                .document(USER_ID_DOCUMENT)
                .collection(TASK_COLLECTION)
                .whereEqualTo("todoRefId", todoRefId)
                .get()
                .await()
                .toObjects(TaskNetwork::class.java)

            ApiResult.Success(data)
        }catch (e: Exception){
            Log.e("TaskFirestoreImpl", "getTasks(todoRefId) - errorMsg: ${e.message}")
            ApiResult.Error(e.message)
        }
    }

    suspend fun deleteTask(taskId: String): ApiResult<Unit> {
        return try {
            firestore
                .collection(ACTIVE_COLLECTION)
                .document(USER_ID_DOCUMENT)
                .collection(TASK_COLLECTION)
                .document(taskId)
                .delete()
                .await()

            ApiResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("TaskFirestoreImpl", "deleteTask - errorMsg: ${e.message}")
            ApiResult.Error(e.message)
        }
    }

    suspend fun deleteTasks(todoRefId: String): ApiResult<Unit> {

       return try {
            val path = firestore.collection(ACTIVE_COLLECTION)
                .document(USER_ID_DOCUMENT)
                .collection(TASK_COLLECTION)

            firestore
                .collection(ACTIVE_COLLECTION)
                .document(USER_ID_DOCUMENT)
                .collection(TASK_COLLECTION)
                .whereEqualTo("todoRefId", todoRefId)
                .get()
                .addOnSuccessListener { tasks ->
                    tasks.forEach { task ->
                        task.reference.delete() }
                }
                .await()

            ApiResult.Success(Unit)
        }catch (e: Exception){
            Log.e("TaskFirestoreImpl", "deleteTasks1 - errorMsg: ${e.message}")
            ApiResult.Error(e.message)
        }

    }

}