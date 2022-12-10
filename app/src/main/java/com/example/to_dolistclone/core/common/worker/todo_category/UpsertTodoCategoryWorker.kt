package com.example.to_dolistclone.core.common.worker.todo_category

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.to_dolistclone.core.common.worker.WorkTag.MAX_RETRY_ATTEMPT
import com.example.to_dolistclone.core.common.worker.WorkTag.UPSERT_TODO_CATEGORY_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.USER_ID_WORKER_DATA
import com.example.to_dolistclone.core.domain.model.TodoCategory
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class UpsertTodoCategoryWorker @AssistedInject constructor(
    @Assisted private val context: Context, @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override suspend fun doWork(): Result {
        val userId = workerParams.inputData.getString(USER_ID_WORKER_DATA)
        val todoCategoryName = workerParams.inputData.getString(UPSERT_TODO_CATEGORY_WORKER_DATA)
        val todoCategory = todoCategoryName?.let { TodoCategory(it) }

        return if (todoCategory == null || userId == null) {
            Log.i("Worker", "UpsertTodoCategoryWorker - failed - null data passed")
            Result.failure()
        } else if (runAttemptCount > MAX_RETRY_ATTEMPT) {
            Log.i("Worker", "UpsertTodoCategoryWorker - failed - exceed retry count")
            Result.failure()
        } else {
            try {
                todoRepository.upsertTodoCategoryNetwork(
                    userId, todoCategory
                )
                Log.i("Worker", "UpsertTodoCategoryWorker - success")
                Result.success()
            } catch (e: Exception) {
                Log.e("Worker", "UpsertTodoCategoryWorker - retrying - ${e.message}")
                Result.retry()
            }
        }
    }

}