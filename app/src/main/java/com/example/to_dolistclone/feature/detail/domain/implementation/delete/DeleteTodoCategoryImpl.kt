package com.example.to_dolistclone.feature.detail.domain.implementation.delete

import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.delete.DeleteTodoCategory
import javax.inject.Inject

class DeleteTodoCategoryImpl @Inject constructor(private val todoRepository: TodoRepository) :
    DeleteTodoCategory {

    override suspend operator fun invoke(todoCategoryName: String): Int =
        todoRepository.deleteTodoCategory(todoCategoryName)

}