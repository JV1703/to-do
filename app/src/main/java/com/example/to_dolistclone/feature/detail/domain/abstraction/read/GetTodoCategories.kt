package com.example.to_dolistclone.feature.detail.domain.abstraction.read

import com.example.to_dolistclone.core.domain.model.TodoCategory
import kotlinx.coroutines.flow.Flow

interface GetTodoCategories {
    operator fun invoke(): Flow<List<TodoCategory>>
}