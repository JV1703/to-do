package com.example.to_dolistclone.core.domain.model.relation.one_to_many

import com.example.to_dolistclone.core.data.local.model.AttachmentEntity
import com.example.to_dolistclone.core.data.local.model.TodoEntity

data class TodoWithAttachments(
    val todo: TodoEntity,
    val attachments: List<AttachmentEntity>
)