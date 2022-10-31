package com.example.to_dolistclone.feature.tasks.domain.abstraction.read

import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoCategoryWithTodos
import kotlinx.coroutines.flow.Flow

interface GetTodoCategoryWithTodos {
    operator fun invoke(todoCategoryName: String): Flow<List<TodoCategoryWithTodos>>
}