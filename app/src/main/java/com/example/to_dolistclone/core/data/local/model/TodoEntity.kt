package com.example.to_dolistclone.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.to_dolistclone.core.domain.model.Todo

@Entity(tableName = "todo")
data class TodoEntity(
    @PrimaryKey(autoGenerate = false)
    val todoId: String,
    val title: String,
    val deadline: Long?,
    val reminder: Long?,
    val repeat: String?,
    val isComplete: Boolean,
    val createdOn: Long?,
    val completedOn: Long?,
    val tasks: Boolean,
    val notes: Boolean,
    val attachments: Boolean,
    val todoCategoryRefName: String
)

fun TodoEntity.toTodo() = Todo(
    todoId,
    title,
    deadline,
    reminder,
    repeat,
    isComplete,
    createdOn,
    completedOn,
    tasks,
    notes,
    attachments,
    todoCategoryRefName
)