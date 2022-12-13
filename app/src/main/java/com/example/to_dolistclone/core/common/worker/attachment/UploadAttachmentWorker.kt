package com.example.to_dolistclone.core.common.worker.attachment

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.common.worker.JsonConverter
import com.example.to_dolistclone.core.common.worker.WORKER_CHANNEL_ID
import com.example.to_dolistclone.core.common.worker.WorkTag
import com.example.to_dolistclone.core.common.worker.WorkTag.ATTACHMENT_ID_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.ATTACHMENT_INITIAL_FILE_PATH_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.UPLOAD_ATTACHMENT_SUCCESS_WORK_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.USER_ID_WORKER_DATA
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject
import kotlin.random.Random.Default.nextInt

@HiltWorker
class UploadAttachmentWorker @AssistedInject constructor(
    @Assisted private val context: Context, @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var jsonConverter: JsonConverter

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            nextInt(), uploadWorkNotification("uploadingAttachment")
        )
    }

    private fun uploadWorkNotification(msg: String): Notification {
        return NotificationCompat.Builder(context, WORKER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification).setContentTitle("Upload attachment")
            .setContentText(msg).build()
    }

    override suspend fun doWork(): Result {
        try {
            setForeground(getForegroundInfo())
        } catch (e: Exception) {
            Log.e("UploadAttachmentWorker", "ForegroundInfo - errorMsg: ${e.message}")
        }

        val userId = workerParams.inputData.getString(USER_ID_WORKER_DATA)
        val attachmentPath =
            workerParams.inputData.getString(ATTACHMENT_INITIAL_FILE_PATH_WORKER_DATA)
        val attachmentUri = attachmentPath?.toUri()
        val attachmentId = workerParams.inputData.getString(ATTACHMENT_ID_WORKER_DATA)
        Log.e("Worker", "UploadAttachmentWorker - attachmentPath: $attachmentPath")

        Log.i("attachmentPath", "UploadAttachmentWorker - path: $attachmentPath")
        Log.i("attachmentPath", "UploadAttachmentWorker - uri: $attachmentUri")

        return if (userId == null || attachmentPath == null || attachmentUri == null) {
            Log.e("Worker", "UploadAttachmentWorker - failed - null data passed")
            attachmentId?.let { todoRepository.deleteAttachment(it) }
            uploadWorkNotification("failed to upload attachment")
            Result.failure()
        } else if (runAttemptCount > WorkTag.MAX_RETRY_ATTEMPT) {
            Log.i("Worker", "UploadAttachmentWorker - failed - exceed retry count")
            uploadWorkNotification("failed to upload attachment")
            attachmentId?.let { todoRepository.deleteAttachment(it) }
            Result.failure()
        } else {
            try {
                todoRepository.uploadAttachment(userId, attachmentUri)
                Log.i("Worker", "UploadAttachmentWorker - success")
                uploadWorkNotification("Attachment successfully uploaded")
                Result.success(workDataOf(UPLOAD_ATTACHMENT_SUCCESS_WORK_DATA to UPLOAD_ATTACHMENT_SUCCESS_WORK_DATA))
            } catch (e: Exception) {
                Log.i("Worker", "UploadAttachmentWorker - retrying - ${e.message}")
                Result.retry()
            }
        }
    }

}