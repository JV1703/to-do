package com.example.to_dolistclone.feature.detail.domain.implementation.create_update

import com.example.to_dolistclone.core.domain.model.Attachment
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.create_update.InsertAttachment
import javax.inject.Inject

class InsertAttachmentImpl @Inject constructor(private val todoRepository: TodoRepository) :
    InsertAttachment {

    override suspend operator fun invoke(attachment:Attachment): Long = todoRepository.insertAttachment(attachment)

}