package com.example.to_dolistclone.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.to_dolistclone.core.domain.model.Attachment

@Entity(tableName = "attachment")
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = false)
    val attachmentId: String,
    val uri: String,
    val todoRefId: String
)

fun AttachmentEntity.toAttachment() = Attachment(
    attachmentId = this.attachmentId,
    uri = this.uri,
    todoRefId = this.todoRefId
)
