package com.example.to_dolistclone.core.data.local.model.relations.many_to_many

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.to_dolistclone.core.data.local.model.AttachmentEntity
import com.example.to_dolistclone.core.data.local.model.TodoEntity

data class TodosWithAttachmentsEntity(
    @Embedded val todo: TodoEntity,
    @Relation(
        parentColumn = "attachmentId",
        entityColumn = "todoRefId",
        associateBy = Junction(TodoAttachmentCrossRefEntity::class)
    )
    val attachments: List<AttachmentEntity>
)