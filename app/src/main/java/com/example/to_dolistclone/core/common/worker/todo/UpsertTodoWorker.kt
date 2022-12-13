package com.example.to_dolistclone.core.common.worker.todo

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.to_dolistclone.core.common.worker.WorkTag.TODO_ID_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.MAX_RETRY_ATTEMPT
import com.example.to_dolistclone.core.common.worker.WorkTag.USER_ID_WORKER_DATA
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltWorker
class UpsertTodoWorker @AssistedInject constructor(
    @Assisted private val context: Context, @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var todoRepository: TodoRepository

    override suspend fun doWork(): Result {
        val userId = workerParams.inputData.getString(USER_ID_WORKER_DATA)
        val todoId = workerParams.inputData.getString(TODO_ID_WORKER_DATA)
        val todo = todoId?.let { todoRepository.getTodo(todoId).first() }
        Log.i("Worker", "UpsertTodoWorker - data - userId: $userId, todoId: $todoId")
        return if (todo == null || userId == null) {
            Log.e("Worker", "UpsertTodoWorker - failed - null data passed")
            Result.failure()
        } else if (runAttemptCount > MAX_RETRY_ATTEMPT) {
            Log.i("Worker", "UpsertTodoWorker - failed - exceed retry count")
            Result.failure()
        } else {
            try {
                todoRepository.upsertTodoNetwork(
                    userId, todo
                )
                Log.i("Worker", "UpsertTodoWorker - success")
                Result.success()
            } catch (e: Exception) {
                Log.e("Worker", "UpsertTodoWorker - retrying - ${e.message}")
                Result.retry()
            }
        }
    }

}