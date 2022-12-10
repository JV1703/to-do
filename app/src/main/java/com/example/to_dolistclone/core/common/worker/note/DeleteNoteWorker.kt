package com.example.to_dolistclone.core.common.worker.note

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
class DeleteNoteWorker @AssistedInject constructor(
    @Assisted private val context: Context, @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var todoRepository: TodoRepository

    override suspend fun doWork(): Result {
        Log.e("Worker", "DeleteNoteWorker - work received")
        val userId = workerParams.inputData.getString(WorkTag.USER_ID_WORKER_DATA)
        val noteId = workerParams.inputData.getString(WorkTag.NOTE_ID_WORKER_DATA)

        return if (userId == null || noteId == null) {
            Log.e("Worker", "DeleteNoteWorker - failed - null data passed")
            Result.failure()
        } else if (runAttemptCount > WorkTag.MAX_RETRY_ATTEMPT) {
            Log.i("Worker", "DeleteNoteWorker - failed - exceed retry count")
            Result.failure()
        } else {
            try {
                todoRepository.deleteNoteNetwork(userId, noteId)
                Log.i("Worker", "DeleteNoteWorker - success")
                Result.success()
            } catch (e: Exception) {
                Log.e("Worker", "DeleteNoteWorker - retrying - ${e.message}")
                Result.retry()
            }
        }
    }
}