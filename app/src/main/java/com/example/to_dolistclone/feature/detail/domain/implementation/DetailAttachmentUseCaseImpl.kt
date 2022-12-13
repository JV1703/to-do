package com.example.to_dolistclone.feature.detail.domain.implementation

import android.net.Uri
import android.util.Log
import androidx.work.WorkManager
import com.example.to_dolistclone.core.common.worker.WorkerManager
import com.example.to_dolistclone.core.data.local.GenericCacheError.GENERIC_CACHE_ERROR
import com.example.to_dolistclone.core.data.local.handleCacheResponse
import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.Attachment
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.DetailAttachmentUseCase
import com.example.to_dolistclone.feature.detail.domain.abstraction.DetailTodoUseCase
import javax.inject.Inject

class DetailAttachmentUseCaseImpl @Inject constructor(
    private val todoRepository: TodoRepository,
    private val detailTodoUseCase: DetailTodoUseCase,
    private val workerManager: WorkerManager,
    private val workManager: WorkManager
) : DetailAttachmentUseCase {

    override suspend fun insertAttachment(
        userId: String, attachment: Attachment, todoUpdatedOn: Long
    ): Async<Long> {
        val cacheResult = todoRepository.insertAttachment(attachment)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                detailTodoUseCase.updateTodoUpdatedOn(userId, attachment.todoRefId, todoUpdatedOn)
                workerManager.upsertAttachment(
                    userId = userId, attachmentId = attachment.attachmentId
                )
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun insertAttachmentToCache(userId: String, attachment: Attachment, todoUpdatedOn: Long){
        todoRepository.insertAttachment(attachment)
    }

    override suspend fun uploadAttachment(
        userId: String,
        initialFileUri: Uri,
        internalStoragePath: String,
        attachmentId: String,
        todoUpdatedOn: Long
    ) {

        Log.i("attachmentPath", "DetailAttachmentUseCaseImpl - $initialFileUri")
        workerManager.uploadAttachment(
            userId = userId,
            initialFileUri = initialFileUri,
            internalStoragePath = internalStoragePath,
            attachmentId = attachmentId
        )
        detailTodoUseCase.updateTodoUpdatedOn(userId, attachmentId, todoUpdatedOn)
    }

    override suspend fun deleteAttachment(
        userId: String, attachmentId: String, todoId: String, todoUpdatedOn: Long, networkPath: String
    ): Async<Int> {
        val cacheResult = todoRepository.deleteAttachment(attachmentId)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                workerManager.deleteAttachment(userId = userId, attachmentId = attachmentId, networkPath = networkPath)
                detailTodoUseCase.updateTodoUpdatedOn(userId, todoId, todoUpdatedOn)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }


}