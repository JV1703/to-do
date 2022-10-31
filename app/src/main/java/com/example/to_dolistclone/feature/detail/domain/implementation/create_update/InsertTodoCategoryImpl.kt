package com.example.to_dolistclone.feature.detail.domain.implementation.create_update

import com.example.to_dolistclone.core.domain.model.TodoCategory
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.create_update.InsertTodoCategory
import javax.inject.Inject

class InsertTodoCategoryImpl @Inject constructor(private val todoRepository: TodoRepository) :
    InsertTodoCategory {

    override suspend operator fun invoke(category: TodoCategory): Long = todoRepository.insertTodoCategory(category)

}