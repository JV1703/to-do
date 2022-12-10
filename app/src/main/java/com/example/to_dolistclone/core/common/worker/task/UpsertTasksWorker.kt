package com.example.to_dolistclone.core.common.worker.task

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.to_dolistclone.core.common.worker.WorkTag
import com.example.to_dolistclone.core.common.worker.WorkTag.TODO_ID_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.USER_ID_WORKER_DATA
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltWorker
class UpsertTasksWorker @AssistedInject constructor(
    @Assisted private val context: Context, @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var todoRepository: TodoRepository

    override suspend fun doWork(): Result {
        Log.e("Worker", "UpsertTasksWorker - work received")
        val userId = workerParams.inputData.getString(USER_ID_WORKER_DATA)
        val todoId = workerParams.inputData.getString(TODO_ID_WORKER_DATA)
        val tasks = todoId?.let {
            todoRepository.getTodoWithTasks(todoId).map { it?.tasks?: emptyList() }.first()
        }

        return if (userId == null || tasks == null) {
            Log.e("Worker", "UpsertTasksWorker - failed - null data passed")
            Result.failure()
        } else if (runAttemptCount > WorkTag.MAX_RETRY_ATTEMPT) {
            Log.i("Worker", "UpsertTasksWorker - failed - exceed retry count")
            Result.failure()
        } else {
            try {
                tasks.forEach { task ->
                    todoRepository.upsertTaskNetwork(userId, task)
                }
                Log.i("Worker", "UpsertTasksWorker - success")
                Result.success()
            } catch (e: Exception) {
                Log.e("Worker", "UpsertTasksWorker - retrying - ${e.message}")
                Result.retry()
            }
        }
    }
}