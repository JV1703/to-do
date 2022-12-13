package com.example.to_dolistclone.feature.detail.domain.implementation

import android.net.Uri
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

    override suspend fun uploadAttachment(userId: String, attachmentUri: Uri){
        todoRepository.uploadAttachment(userId, attachmentUri = attachmentUri)
    }

    override suspend fun uploadAttachment(
        userId: String,
        initialFileUri: Uri,
        internalStoragePath: String,
        todoRefId: String,
        todoUpdatedOn: Long
    ) {
        workerManager.uploadAttachment(
            userId = userId,
            initialFileUri = initialFileUri,
            internalStoragePath = internalStoragePath,
            todoRefId = todoRefId
        )
        detailTodoUseCase.updateTodoUpdatedOn(userId, todoRefId, todoUpdatedOn)
    }

    override suspend fun deleteAttachment(
        userId: String, attachmentId: String, todoId: String, todoUpdatedOn: Long
    ): Async<Int> {
        val cacheResult = todoRepository.deleteAttachment(attachmentId)
        return handleCacheResponse(cacheResult) { resultObj ->
            if (resultObj > 0) {
                detailTodoUseCase.updateTodoUpdatedOn(userId, todoId, todoUpdatedOn)
                workerManager.deleteAttachment(userId = userId, attachmentId = attachmentId)
                Async.Success(resultObj)
            } else {
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }


}