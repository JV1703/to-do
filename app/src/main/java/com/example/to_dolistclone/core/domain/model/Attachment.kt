package com.example.to_dolistclone.core.domain.model

import com.example.to_dolistclone.core.data.local.model.AttachmentEntity

data class Attachment(
    val attachmentId: String,
    val name: String,
    val uri: String,
    val type: String,
    val size: Long,
    val todoRefId: String
)

fun Attachment.toAttachmentEntity() = AttachmentEntity(
    attachmentId = attachmentId,
    name = name,
    uri = uri,
    type = type,
    size = size,
    todoRefId = todoRefId
)