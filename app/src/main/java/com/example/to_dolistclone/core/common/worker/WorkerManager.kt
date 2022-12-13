package com.example.to_dolistclone.core.common.worker

import android.net.Uri
import androidx.work.*
import com.example.to_dolistclone.core.common.worker.WorkTag.ATTACHMENT_ID_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.NOTE_ID_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.TASK_ID_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.TODO_ID_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.ATTACHMENT_INTERNAL_STORAGE_PATH_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.ATTACHMENT_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.ATTACHMENT_INITIAL_FILE_PATH_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.UPSERT_TASK_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.UPSERT_TODO_CATEGORY_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.WorkTag.USER_ID_WORKER_DATA
import com.example.to_dolistclone.core.common.worker.attachment.DeleteAttachmentWorker
import com.example.to_dolistclone.core.common.worker.attachment.UploadAttachmentWorker
import com.example.to_dolistclone.core.common.worker.attachment.UpsertAttachmentCacheWorker
import com.example.to_dolistclone.core.common.worker.attachment.UpsertAttachmentNetworkWorker
import com.example.to_dolistclone.core.common.worker.note.DeleteNoteWorker
import com.example.to_dolistclone.core.common.worker.note.UpsertNoteWorker
import com.example.to_dolistclone.core.common.worker.task.DeleteTaskWorker
import com.example.to_dolistclone.core.common.worker.task.UpsertTaskWorker
import com.example.to_dolistclone.core.common.worker.task.UpsertTasksWorker
import com.example.to_dolistclone.core.common.worker.todo.DeleteTodoWorker
import com.example.to_dolistclone.core.common.worker.todo.UpsertTodoWorker
import com.example.to_dolistclone.core.common.worker.todo_category.UpsertTodoCategoryWorker
import com.example.to_dolistclone.core.domain.model.Attachment
import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import javax.inject.Inject

val WORKER_CHANNEL_ID = "Work manager"

class WorkerManager @Inject constructor(
    private val workManager: WorkManager, private val jsonConverter: JsonConverter
) {

    fun upsertTodoCategory(userId: String, todoCategoryName: String) {
        val request =
            OneTimeWorkRequestBuilder<UpsertTodoCategoryWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setInputData(
                    workDataOf(
                        USER_ID_WORKER_DATA to userId,
                        UPSERT_TODO_CATEGORY_WORKER_DATA to todoCategoryName
                    )
                ).setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                ).build()

        workManager.enqueueUniqueWork(
            "test upsertTodoCategoryRequest to Network",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            request
        )
    }

    fun upsertTodo(userId: String, todoId: String) {
        val request =
            OneTimeWorkRequestBuilder<UpsertTodoWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setInputData(
                    workDataOf(
                        USER_ID_WORKER_DATA to userId, TODO_ID_WORKER_DATA to todoId
                    )
                ).setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                ).build()

        workManager.enqueueUniqueWork(
            "test insertTodo to Network", ExistingWorkPolicy.APPEND_OR_REPLACE, request
        )
    }

//    fun updateTodo(userId: String, todoId: String, field: Pair<String, Any?>) {
//        val workerHelper = WorkerHelper(
//            key = field.first, value = field.second
//        )
//        val data = jsonConverter.toJson(workerHelper)
//        val request =
//            OneTimeWorkRequestBuilder<UpdateTodoWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//                .setConstraints(
//                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
//                ).setInputData(
//                    workDataOf(
//                        USER_ID_WORKER_DATA to userId,
//                        TODO_ID_WORKER_DATA to todoId,
//                        FIELD_WORKER_DATA to data
//                    )
//                ).setBackoffCriteria(
//                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
//                ).build()
//
//        workManager.enqueueUniqueWork(
//            "test updateTodo to Network", ExistingWorkPolicy.APPEND_OR_REPLACE, request
//        )
//    }

    fun deleteTodo(userId: String, todoId: String) {
        val request =
            OneTimeWorkRequestBuilder<DeleteTodoWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setInputData(
                    workDataOf(
                        USER_ID_WORKER_DATA to userId, TODO_ID_WORKER_DATA to todoId
                    )
                ).setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                ).build()

        workManager.enqueueUniqueWork(
            "test deleteTodo to Network", ExistingWorkPolicy.APPEND_OR_REPLACE, request
        )
    }

    fun upsertTask(userId: String, taskId: String) {
        val request =
            OneTimeWorkRequestBuilder<UpsertTaskWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setInputData(
                    workDataOf(
                        USER_ID_WORKER_DATA to userId, UPSERT_TASK_WORKER_DATA to taskId
                    )
                ).setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                ).build()

        workManager.enqueueUniqueWork(
            "test upsertTask to Network", ExistingWorkPolicy.APPEND_OR_REPLACE, request
        )
    }

    fun upsertTasks(userId: String, todoRefId: String) {
        val request =
            OneTimeWorkRequestBuilder<UpsertTasksWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setInputData(
                    workDataOf(
                        USER_ID_WORKER_DATA to userId, TODO_ID_WORKER_DATA to todoRefId
                    )
                ).setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                ).build()

        workManager.enqueueUniqueWork(
            "test upsertTasks to Network", ExistingWorkPolicy.APPEND_OR_REPLACE, request
        )
    }

//    fun updateTask(userId: String, taskId: String, field: Pair<String, Any>) {
//        val workerHelper = WorkerHelper(
//            key = field.first, value = field.second
//        )
//        val data = jsonConverter.toJson(workerHelper)
//        Log.i("workManager", "updateTask - $data")
//        val request =
//            OneTimeWorkRequestBuilder<UpdateTaskWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//                .setConstraints(
//                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
//                ).setInputData(
//                    workDataOf(
//                        USER_ID_WORKER_DATA to userId,
//                        TASK_ID_WORKER_DATA to taskId,
//                        FIELD_WORKER_DATA to data
//                    )
//                ).setBackoffCriteria(
//                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
//                ).build()
//
//        workManager.enqueueUniqueWork(
//            "test updateTask to Network", ExistingWorkPolicy.APPEND_OR_REPLACE, request
//        )
//    }

    fun deleteTask(userId: String, taskId: String) {
        val request =
            OneTimeWorkRequestBuilder<DeleteTaskWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setInputData(
                    workDataOf(
                        USER_ID_WORKER_DATA to userId, TASK_ID_WORKER_DATA to taskId
                    )
                ).setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                ).build()

        workManager.enqueueUniqueWork(
            "test deleteTask to Network", ExistingWorkPolicy.APPEND_OR_REPLACE, request
        )
    }

    fun upsertNote(userId: String, noteId: String) {
        val request =
            OneTimeWorkRequestBuilder<UpsertNoteWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setInputData(
                    workDataOf(
                        USER_ID_WORKER_DATA to userId, NOTE_ID_WORKER_DATA to noteId
                    )
                ).setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                ).build()

        workManager.enqueueUniqueWork(
            "test insertNote to Network", ExistingWorkPolicy.APPEND_OR_REPLACE, request
        )
    }

    fun deleteNote(userId: String, noteId: String) {
        val request =
            OneTimeWorkRequestBuilder<DeleteNoteWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setInputData(
                    workDataOf(
                        USER_ID_WORKER_DATA to userId, NOTE_ID_WORKER_DATA to noteId
                    )
                ).setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                ).build()

        workManager.enqueueUniqueWork(
            "test deleteNote to Network", ExistingWorkPolicy.APPEND_OR_REPLACE, request
        )
    }

    fun upsertAttachment(userId: String, attachmentId: String) {
        val request =
            OneTimeWorkRequestBuilder<UpsertAttachmentNetworkWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setInputData(
                    workDataOf(
                        USER_ID_WORKER_DATA to userId, ATTACHMENT_ID_WORKER_DATA to attachmentId
                    )
                ).setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                ).build()

        workManager.enqueueUniqueWork(
            "test upsertAttachment to Network", ExistingWorkPolicy.APPEND_OR_REPLACE, request
        )
    }

    fun uploadAttachment(userId: String, initialFileUri: Uri, internalStoragePath: String, todoRefId: String) {
        val initialFilePath = initialFileUri.toString()

        val uploadRequest =
            OneTimeWorkRequestBuilder<UploadAttachmentWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setInputData(
                    workDataOf(
                        USER_ID_WORKER_DATA to userId,
                        ATTACHMENT_INITIAL_FILE_PATH_WORKER_DATA to initialFilePath,
                        ATTACHMENT_INTERNAL_STORAGE_PATH_WORKER_DATA to internalStoragePath
                    )
                ).setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                ).build()

        val upsertToCacheRequest =
            OneTimeWorkRequestBuilder<UpsertAttachmentCacheWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setInputData(
                    workDataOf(
                        USER_ID_WORKER_DATA to userId,
                        ATTACHMENT_INITIAL_FILE_PATH_WORKER_DATA to initialFilePath
                    )
                ).setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                ).build()

        val upsertToNetworkRequest =
            OneTimeWorkRequestBuilder<UpsertAttachmentNetworkWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setInputData(
                    workDataOf(
                        USER_ID_WORKER_DATA to userId,
                        ATTACHMENT_ID_WORKER_DATA to todoRefId
                    )
                ).setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                ).build()

        workManager.beginUniqueWork(
            "test uploadAttachment, upsertAttachmentToCache, upsertAttachmentToNetwork",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            uploadRequest
        ).then(listOf(upsertToCacheRequest, upsertToNetworkRequest)).enqueue()

    }

    fun deleteAttachment(userId: String, attachmentId: String) {
        val request =
            OneTimeWorkRequestBuilder<DeleteAttachmentWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setInputData(
                    workDataOf(
                        USER_ID_WORKER_DATA to userId, ATTACHMENT_ID_WORKER_DATA to attachmentId
                    )
                ).setBackoffCriteria(
                    BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS
                ).build()

        workManager.enqueueUniqueWork(
            "test deleteAttachment to Network", ExistingWorkPolicy.APPEND_OR_REPLACE, request
        )
    }

}