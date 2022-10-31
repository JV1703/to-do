package com.example.to_dolistclone.core.domain.model

import com.example.to_dolistclone.core.data.local.model.AttachmentEntity

data class Attachment(
    val attachmentId: String,
    val uri: String,
    val todoRefId: String
)

fun Attachment.toAttachmentEntity() = AttachmentEntity(
    attachmentId = attachmentId, uri = uri, todoRefId = todoRefId
)