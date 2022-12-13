package com.example.to_dolistclone.core.common.worker.attachment

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.to_dolistclone.core.common.worker.JsonConverter
import com.example.to_dolistclone.core.common.worker.WorkTag
import com.example.to_dolistclone.core.common.worker.WorkTag.ATTACHMENT_ID_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.USER_ID_WORKER_DATA
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltWorker
class UpsertAttachmentNetworkWorker @AssistedInject constructor(
    @Assisted private val context: Context, @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var jsonConverter: JsonConverter

    override suspend fun doWork(): Result {
        val userId = workerParams.inputData.getString(USER_ID_WORKER_DATA)
        val attachmentId = workerParams.inputData.getString(ATTACHMENT_ID_WORKER_DATA)
        val attachment = attachmentId?.let { todoRepository.getAttachment(it).first() }
        Log.i("Worker", "UpsertAttachmentNetworkWorker - failed - attachmentId: $attachmentId, attachment: $attachment")

        return if (userId == null || attachment == null) {
            Log.e("Worker", "UpsertAttachmentNetworkWorker - failed - null data passed")
            Result.failure()
        } else if (runAttemptCount > WorkTag.MAX_RETRY_ATTEMPT) {
            Log.i("Worker", "UpsertAttachmentNetworkWorker - failed - exceed retry count")
            Result.failure()
        } else {
            try {
                todoRepository.upsertAttachmentNetwork(userId, attachment)
                Log.i("Worker", "UpsertAttachmentNetworkWorker - success")
                Result.success()
            } catch (e: Exception) {
                Log.e("Worker", "UpsertAttachmentNetworkWorker - retrying - ${e.message}")
                Result.retry()
            }
        }
    }

}