package com.example.to_dolistclone.core.data.remote.model

import com.example.to_dolistclone.core.data.local.model.AttachmentEntity

data class AttachmentNetwork(
    val attachmentId: String = "",
    val name: String = "",
    val localUri: String = "",
    val networkUri: String = "",
    val type: String = "",
    val size: Long = 0,
    val todoRefId: String = ""
)

fun AttachmentNetwork.toAttachmentEntity() = AttachmentEntity(
    attachmentId = attachmentId,
    name = name,
    localUri = localUri,
    networkUri = networkUri,
    type = type,
    size = size,
    todoRefId = todoRefId
)