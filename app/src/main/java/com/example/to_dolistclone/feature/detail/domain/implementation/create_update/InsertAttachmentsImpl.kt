package com.example.to_dolistclone.feature.detail.domain.implementation.create_update

import com.example.to_dolistclone.core.domain.model.Attachment
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.create_update.InsertAttachments
import javax.inject.Inject

class InsertAttachmentsImpl @Inject constructor(private val todoRepository: TodoRepository) :
    InsertAttachments {

    override suspend operator fun invoke(attachments: List<Attachment>): LongArray =
        todoRepository.insertAttachments(attachments)

}