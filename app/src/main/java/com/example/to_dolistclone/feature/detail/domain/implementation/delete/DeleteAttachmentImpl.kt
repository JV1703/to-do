package com.example.to_dolistclone.feature.detail.domain.implementation.delete

import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.delete.DeleteAttachment
import javax.inject.Inject

class DeleteAttachmentImpl @Inject constructor(private val todoRepository: TodoRepository) :
    DeleteAttachment {

    override suspend operator fun invoke(attachmentId: String): Int = todoRepository.deleteAttachment(attachmentId)

}