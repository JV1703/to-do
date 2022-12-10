package com.example.to_dolistclone.core.common.worker.task

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.to_dolistclone.core.common.worker.WorkTag
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltWorker
class UpsertTaskWorker @AssistedInject constructor(
    @Assisted private val context: Context, @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var todoRepository: TodoRepository

    override suspend fun doWork(): Result {
        val userId = workerParams.inputData.getString(WorkTag.USER_ID_WORKER_DATA)
        val taskId = workerParams.inputData.getString(WorkTag.UPSERT_TASK_WORKER_DATA)
        val task = taskId?.let {
            todoRepository.getTask(taskId).first()
        }

        return if (userId == null || task == null) {
            Log.e("Worker", "UpsertTaskWorker - failed - null data passed")
            Result.failure()
        } else if (runAttemptCount > WorkTag.MAX_RETRY_ATTEMPT) {
            Log.i("Worker", "UpsertTaskWorker - failed - exceed retry count")
            Result.failure()
        } else {
            try {
                todoRepository.upsertTaskNetwork(userId, task)
                Log.i("Worker", "UpsertTaskWorker - success")
                Result.success()
            } catch (e: Exception) {
                Log.e("Worker", "UpsertTaskWorker - retrying - ${e.message}")
                Result.retry()
            }
        }
    }

}