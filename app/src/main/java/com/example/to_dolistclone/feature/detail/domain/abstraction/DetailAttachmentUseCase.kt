package com.example.to_dolistclone.feature.detail.domain.abstraction

import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.Attachment

interface DetailAttachmentUseCase {

    suspend fun insertAttachment(userId: String, attachment: Attachment, todoUpdatedOn: Long): Async<Long>
    suspend fun deleteAttachment(userId: String, attachmentId: String, todoId: String, todoUpdatedOn: Long): Async<Int>

}