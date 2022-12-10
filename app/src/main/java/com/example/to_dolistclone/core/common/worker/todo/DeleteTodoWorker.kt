package com.example.to_dolistclone.core.common.worker.todo

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.to_dolistclone.core.common.worker.WorkTag
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class DeleteTodoWorker @AssistedInject constructor(
    @Assisted private val context: Context, @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var todoRepository: TodoRepository

    override suspend fun doWork(): Result {
        Log.e("Worker", "DeleteTodoWorker - work received")
        val userId = workerParams.inputData.getString(WorkTag.USER_ID_WORKER_DATA)
        val todoId = workerParams.inputData.getString(WorkTag.TODO_ID_WORKER_DATA)

        return if (userId == null || todoId == null) {
            Log.e("Worker", "DeleteTodoWorker - failed - null data passed")
            Result.failure()
        } else if (runAttemptCount > WorkTag.MAX_RETRY_ATTEMPT) {
            Log.i("Worker", "DeleteTodoWorker - failed - exceed retry count")
            Result.failure()
        } else {
            try {
                todoRepository.deleteTodoNetwork(userId, todoId)
                Log.i("Worker", "DeleteTodoWorker - success")
                Result.success()
            } catch (e: Exception) {
                Log.e("Worker", "DeleteTodoWorker - retrying - ${e.message}")
                Result.retry()
            }
        }
    }
}