package com.example.to_dolistclone.core.common.worker.attachment

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.to_dolistclone.core.common.FileManager
import com.example.to_dolistclone.core.common.worker.JsonConverter
import com.example.to_dolistclone.core.common.worker.WorkTag
import com.example.to_dolistclone.core.common.worker.WorkTag.ATTACHMENT_INITIAL_FILE_PATH_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.USER_ID_WORKER_DATA
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class UpsertAttachmentCacheWorker @AssistedInject constructor(
    @Assisted private val context: Context, @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var jsonConverter: JsonConverter

    @Inject
    lateinit var fileManager: FileManager

    override suspend fun doWork(): Result {
        val userId = workerParams.inputData.getString(USER_ID_WORKER_DATA)
        val originalFilePath =
            workerParams.inputData.getString(ATTACHMENT_INITIAL_FILE_PATH_WORKER_DATA)

        return if (userId == null || originalFilePath == null) {
            Log.e("Worker", "UpsertAttachmentCacheWorker - failed - null data passed")
            Result.failure()
        } else if (runAttemptCount > WorkTag.MAX_RETRY_ATTEMPT) {
            Log.i("Worker", "UpsertAttachmentCacheWorker - failed - exceed retry count")
            Result.failure()
        } else {
            try {
                fileManager.copyToInternalStorage(originalFileUri = originalFilePath.toUri()) // need to get original file location
                Log.i("Worker", "UpsertAttachmentCacheWorker - success")
                Result.success()
            } catch (e: Exception) {
                Log.e("Worker", "UpsertAttachmentCacheWorker - retrying - ${e.message}")
                Result.retry()
            }
        }
    }

}