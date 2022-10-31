package com.example.to_dolistclone.core.domain.model.relation.one_to_many

import com.example.to_dolistclone.core.data.local.model.TodoCategoryEntity
import com.example.to_dolistclone.core.data.local.model.TodoEntity

data class TodoCategoryWithTodos(
    val todoCategory: TodoCategoryEntity,
    val todos: List<TodoEntity>
)