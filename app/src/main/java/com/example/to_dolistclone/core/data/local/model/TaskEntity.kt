package com.example.to_dolistclone.core.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.to_dolistclone.core.domain.model.Task

@Entity(
    tableName = "task",
    foreignKeys = [ForeignKey(
        entity = TodoEntity::class,
        parentColumns = arrayOf("todoId"),
        childColumns = arrayOf("todoRefId"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = false) val taskId: String,
    val task: String,
    val isComplete: Boolean = false,
    val position: Int,
    val todoRefId: String?
)

fun TaskEntity.toTask() = Task(
    taskId = taskId, task = task, isComplete = isComplete,position = position, todoRefId = todoRefId
)