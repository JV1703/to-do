package com.example.to_dolistclone.core.common.worker.attachment

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.to_dolistclone.core.common.worker.WorkTag
import com.example.to_dolistclone.core.common.worker.WorkTag.ATTACHMENT_ID_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.USER_ID_WORKER_DATA
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

class DeleteAttachmentWorker @AssistedInject constructor(
    @Assisted private val context: Context, @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var todoRepository: TodoRepository

    override suspend fun doWork(): Result {
        Log.e("Worker", "DeleteAttachmentWorker - work received")
        val userId = workerParams.inputData.getString(USER_ID_WORKER_DATA)
        val attachmentId = workerParams.inputData.getString(ATTACHMENT_ID_WORKER_DATA)

        return if (userId == null || attachmentId == null) {
            Log.e("Worker", "DeleteAttachmentWorker - failed - null data passed")
            Result.failure()
        } else if (runAttemptCount > WorkTag.MAX_RETRY_ATTEMPT) {
            Log.i("Worker", "DeleteAttachmentWorker - failed - exceed retry count")
            Result.failure()
        } else {
            try {
                todoRepository.deleteNoteNetwork(userId, attachmentId)
                Log.i("Worker", "DeleteAttachmentWorker - success")
                Result.success()
            } catch (e: Exception) {
                Log.e("Worker", "DeleteAttachmentWorker - retrying - ${e.message}")
                Result.retry()
            }
        }
    }
}