package com.example.to_dolistclone.core.data.local.model.relations.many_to_many

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.to_dolistclone.core.data.local.model.AttachmentEntity
import com.example.to_dolistclone.core.data.local.model.TodoEntity

class AttachmentsWithTodosEntity(
    @Embedded
    val attachment: AttachmentEntity,
    @Relation(
        parentColumn = "todoRefId",
        entityColumn = "attachmentId",
        associateBy = Junction(TodoAttachmentCrossRefEntity::class)
    )
    val todos: List<TodoEntity>
)