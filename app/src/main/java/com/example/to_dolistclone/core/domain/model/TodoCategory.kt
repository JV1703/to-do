package com.example.to_dolistclone.core.domain.model

import com.example.to_dolistclone.core.data.local.model.TodoCategoryEntity

data class TodoCategory(
    val todoCategoryName: String
)

fun TodoCategory.toTodoEntity() = TodoCategoryEntity(
    todoCategoryName = todoCategoryName
)