package com.example.to_dolistclone.feature.detail.domain.implementation.delete

import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.delete.DeleteTodo
import javax.inject.Inject

class DeleteTodoImpl @Inject constructor(private val todoRepository: TodoRepository) : DeleteTodo {

    override suspend operator fun invoke(todoId: String): Int = todoRepository.deleteTodo(todoId)

}