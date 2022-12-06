package com.example.to_dolistclone.feature.common.domain.todo

import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import kotlinx.coroutines.flow.Flow

abstract class BaseTodoUseCaseImpl(private val todoRepository: TodoRepository) {

    val todos = todoRepository.getTodos()

    suspend fun saveSelectedTodoId(todoId: String) {
        todoRepository.saveSelectedTodoId(todoId)
    }

    fun getSelectedTodoId(): Flow<String> = todoRepository.getSelectedTodoId()

}