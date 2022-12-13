package com.example.to_dolistclone.core.common.worker.attachment

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.to_dolistclone.core.common.worker.JsonConverter
import com.example.to_dolistclone.core.common.worker.WorkTag
import com.example.to_dolistclone.core.common.worker.WorkTag.ATTACHMENT_INITIAL_FILE_PATH_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.UPLOAD_ATTACHMENT_SUCCESS_WORK_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.USER_ID_WORKER_DATA
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class UploadAttachmentWorker @AssistedInject constructor(
    @Assisted private val context: Context, @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var jsonConverter: JsonConverter

    override suspend fun doWork(): Result {
        val userId = workerParams.inputData.getString(USER_ID_WORKER_DATA)
        val attachmentPath =
            workerParams.inputData.getString(ATTACHMENT_INITIAL_FILE_PATH_WORKER_DATA)
        val attachmentUri = attachmentPath?.toUri()
        Log.e("Worker", "UploadAttachmentWorker - attachmentPath: $attachmentPath")

        Log.i("attachmentPath", "UploadAttachmentWorker - path: $attachmentPath")
        Log.i("attachmentPath", "UploadAttachmentWorker - uri: $attachmentUri")

        return if (userId == null || attachmentPath == null || attachmentUri == null) {
            Log.e("Worker", "UploadAttachmentWorker - failed - null data passed")
            Result.failure()
        } else if (runAttemptCount > WorkTag.MAX_RETRY_ATTEMPT) {
            Log.i("Worker", "UploadAttachmentWorker - failed - exceed retry count")
            Result.failure()
        } else {
            try {
                todoRepository.uploadAttachment(userId, attachmentUri)
                Log.i("Worker", "UploadAttachmentWorker - success")
                Result.success(workDataOf(UPLOAD_ATTACHMENT_SUCCESS_WORK_DATA to UPLOAD_ATTACHMENT_SUCCESS_WORK_DATA))
            } catch (e: Exception) {
                Log.e("Worker", "UploadAttachmentWorker - retrying - ${e.message}")
                Result.retry()
            }
        }
    }

}