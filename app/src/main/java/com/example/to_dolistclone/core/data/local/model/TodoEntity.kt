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
    val reminder: Boolean = false,
    val repeat: String? = null,
    val isComplete: Boolean = false,
    val createdOn: Long?,
    val completedOn: Long?,
    val tasks: Boolean = false,
    val note: Boolean = false,
    val attachments: Boolean = false,
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
    note,
    attachments,
    todoCategoryRefName
)