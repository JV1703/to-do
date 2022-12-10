package com.example.to_dolistclone.feature.detail.domain.implementation

import com.example.to_dolistclone.core.common.worker.WorkerHelper
import com.example.to_dolistclone.core.common.worker.WorkerManager
import com.example.to_dolistclone.core.data.local.GenericCacheError.GENERIC_CACHE_ERROR
import com.example.to_dolistclone.core.data.local.handleCacheResponse
import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.Attachment
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.DetailAttachmentUseCase
import javax.inject.Inject

class DetailAttachmentUseCaseImpl @Inject constructor(private val todoRepository: TodoRepository, private val workerManager: WorkerManager) :
    DetailAttachmentUseCase {

    override suspend fun insertAttachment(userId: String, attachment: Attachment): Async<Long> {
        val cacheResult = todoRepository.insertAttachment(attachment)
        return handleCacheResponse(cacheResult){resultObj ->
            if(resultObj > 0){
                workerManager.upsertAttachment(userId = userId, attachmentId = attachment.attachmentId)
                Async.Success(resultObj)
            }else{
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun deleteAttachment(userId: String, attachmentId: String): Async<Int> {
        val cacheResult = todoRepository.deleteAttachment(attachmentId)
        return handleCacheResponse(cacheResult) { resultObj ->
            if(resultObj> 0){
                workerManager.deleteAttachment(userId = userId, attachmentId = attachmentId)
                Async.Success(resultObj)
            }else{
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }


}