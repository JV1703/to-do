package com.example.to_dolistclone.core.domain.model.relation.one_to_many

import com.example.to_dolistclone.core.domain.model.Task
import com.example.to_dolistclone.core.domain.model.Todo

data class TodoWithTasks(
    val todo: Todo, val tasks: List<Task>
)