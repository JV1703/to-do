package com.example.to_dolistclone.feature.detail.domain.abstraction

import android.net.Uri
import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.Attachment

interface DetailAttachmentUseCase {

    suspend fun insertAttachment(
        userId: String, attachment: Attachment, todoUpdatedOn: Long
    ): Async<Long>

    suspend fun deleteAttachment(
        userId: String, attachmentId: String, todoId: String, todoUpdatedOn: Long, networkPath: String
    ): Async<Int>

    suspend fun uploadAttachment(
        userId: String,
        initialFileUri: Uri,
        internalStoragePath: String,
        attachmentId: String,
        todoUpdatedOn: Long
    )

    suspend fun insertAttachmentToCache(userId: String, attachment: Attachment, todoUpdatedOn: Long)
}