package com.example.to_dolistclone.core.domain.model

import com.example.to_dolistclone.core.data.local.model.AttachmentEntity
import com.example.to_dolistclone.core.data.remote.model.AttachmentNetwork

data class Attachment(
    val attachmentId: String,
    val name: String,
    val localUri: String,
    val networkUri: String,
    val type: String,
    val size: Long,
    val todoRefId: String
)

fun Attachment.toAttachmentEntity() = AttachmentEntity(
    attachmentId = attachmentId,
    name = name,
    localUri = localUri,
    networkUri = networkUri,
    type = type,
    size = size,
    todoRefId = todoRefId
)

fun Attachment.toAttachmentNetwork() = AttachmentNetwork(
    attachmentId = attachmentId,
    name = name,
    localUri = localUri,
    networkUri = networkUri,
    type = type,
    size = size,
    todoRefId = todoRefId
)