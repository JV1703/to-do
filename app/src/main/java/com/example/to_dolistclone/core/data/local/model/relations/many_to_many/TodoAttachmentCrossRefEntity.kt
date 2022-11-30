package com.example.to_dolistclone.core.data.local.model.relations.many_to_many

import androidx.room.Entity

@Entity(primaryKeys = ["attachmentId, todoRefId"])
data class TodoAttachmentCrossRefEntity(
    val attachmentId: String,
    val todoRefId: String

)