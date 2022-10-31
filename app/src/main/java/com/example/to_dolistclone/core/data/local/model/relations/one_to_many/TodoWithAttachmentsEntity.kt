package com.example.to_dolistclone.core.data.local.model.relations.one_to_many

import androidx.room.Embedded
import androidx.room.Relation
import com.example.to_dolistclone.core.data.local.model.AttachmentEntity
import com.example.to_dolistclone.core.data.local.model.TodoEntity
import com.example.to_dolistclone.core.data.local.model.toAttachment
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoWithAttachments

data class TodoWithAttachmentsEntity(
    @Embedded val todo: TodoEntity,
    @Relation(
        parentColumn = "todoId",
        entityColumn = "todoRefId"
    )
    val attachments: List<AttachmentEntity>
)

fun TodoWithAttachmentsEntity.toAttachments() = this.attachments.map {
    it.toAttachment()
}

fun TodoWithAttachmentsEntity.toTodoWithAttachments() = TodoWithAttachments(
    todo = todo, attachments = attachments
)