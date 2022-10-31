package com.example.to_dolistclone.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.to_dolistclone.core.domain.model.TodoCategory

@Entity(tableName = "todo_category")
data class TodoCategoryEntity(
    @PrimaryKey(autoGenerate = false)
    val todoCategoryName: String
)

fun TodoCategoryEntity.toTodoCategory() = TodoCategory(
    todoCategoryName = this.todoCategoryName
)
