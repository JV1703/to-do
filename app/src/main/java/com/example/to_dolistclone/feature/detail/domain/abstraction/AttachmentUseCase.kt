package com.example.to_dolistclone.feature.detail.domain.abstraction

import com.example.to_dolistclone.core.domain.model.Attachment

interface AttachmentUseCase {

    suspend fun insertAttachment(attachment: Attachment): Long
    suspend fun insertAttachments(attachments: List<Attachment>): LongArray
    suspend fun deleteAttachment(attachmentId: String): Int

}