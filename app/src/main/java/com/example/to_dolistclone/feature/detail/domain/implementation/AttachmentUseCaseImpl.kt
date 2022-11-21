package com.example.to_dolistclone.feature.detail.domain.implementation

import com.example.to_dolistclone.core.domain.model.Attachment
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.AttachmentUseCase
import javax.inject.Inject

class AttachmentUseCaseImpl @Inject constructor(private val todoRepository: TodoRepository): AttachmentUseCase {

    override suspend fun insertAttachment(attachment: Attachment): Long = todoRepository.insertAttachment(attachment)

    override suspend fun insertAttachments(attachments: List<Attachment>): LongArray = todoRepository.insertAttachments(attachments)

    override suspend fun deleteAttachment(attachmentId: String): Int = todoRepository.deleteAttachment(attachmentId)

}