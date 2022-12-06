package com.example.to_dolistclone.core.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.to_dolistclone.core.domain.model.Attachment

@Entity(tableName = "attachment",
    foreignKeys = [ForeignKey(
        entity = TodoEntity::class,
        parentColumns = arrayOf("todoId"),
        childColumns = arrayOf("todoRefId"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )])
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = false)
    val attachmentId: String,
    val name: String,
    val uri: String,
    val type: String,
    val size: Long,
    val todoRefId: String
)

fun AttachmentEntity.toAttachment() = Attachment(
    attachmentId = attachmentId,
    name = name,
    uri = uri,
    type = type,
    size = size,
    todoRefId = todoRefId
)
