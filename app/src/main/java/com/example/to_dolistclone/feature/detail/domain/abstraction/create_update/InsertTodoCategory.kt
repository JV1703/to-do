package com.example.to_dolistclone.feature.detail.domain.abstraction.create_update

import com.example.to_dolistclone.core.domain.model.TodoCategory

interface InsertTodoCategory {
    suspend operator fun invoke(category: TodoCategory): Long
}