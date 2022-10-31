package com.example.to_dolistclone.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.to_dolistclone.core.domain.model.Task

@Entity(tableName = "task")
class TaskEntity(
    @PrimaryKey(autoGenerate = false) val taskId: Int,
    val task: String,
    val isComplete: Boolean = false,
    val todoRefId: String
)

fun TaskEntity.toSubTask() = Task(
    taskId = taskId, task = task, isComplete = isComplete, todoRefId = todoRefId
)