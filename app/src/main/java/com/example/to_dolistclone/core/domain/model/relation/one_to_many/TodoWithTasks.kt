package com.example.to_dolistclone.core.domain.model.relation.one_to_many

import com.example.to_dolistclone.core.data.local.model.TaskEntity
import com.example.to_dolistclone.core.data.local.model.TodoEntity

data class TodoWithTasks(
    val todo: TodoEntity,
    val tasks: List<TaskEntity>
)