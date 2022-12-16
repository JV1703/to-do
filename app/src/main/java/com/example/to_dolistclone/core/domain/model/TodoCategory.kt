package com.example.to_dolistclone.core.domain.model

import com.example.to_dolistclone.core.data.local.model.TodoCategoryEntity
import com.example.to_dolistclone.core.data.remote.model.TodoCategoryNetwork

data class TodoCategory(
    val todoCategoryName: String
)

fun TodoCategory.toTodoCategoryEntity() = TodoCategoryEntity(
    todoCategoryName = todoCategoryName
)

fun TodoCategory.toTodoCategoryNetwork() = TodoCategoryNetwork(
    todoCategoryName = todoCategoryName
)