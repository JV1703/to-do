package com.example.to_dolistclone.core.data.remote.model

import com.example.to_dolistclone.core.data.local.model.TodoCategoryEntity

data class TodoCategoryNetwork(
    val todoCategoryName: String = ""
)

fun TodoCategoryNetwork.toTodoCategoryEntity() = TodoCategoryEntity(
    todoCategoryName = todoCategoryName
)