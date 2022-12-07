package com.example.to_dolistclone.core.data.remote.model

import com.example.to_dolistclone.core.data.local.model.AttachmentEntity

data class AttachmentNetwork(
    val attachmentId: String = "",
    val name: String = "",
    val uri: String = "",
    val type: String = "",
    val size: Long = 0,
    val todoRefId: String = ""
)

fun AttachmentNetwork.toAttachmentEntity() = AttachmentEntity(
    attachmentId = attachmentId,
    name = name,
    uri = uri,
    type = type,
    size = size,
    todoRefId = todoRefId
)