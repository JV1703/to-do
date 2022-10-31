package com.example.to_dolistclone.feature.detail.domain.implementation.create_update

import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.create_update.InsertTodo
import javax.inject.Inject

class InsertTodoImpl @Inject constructor(private val todoRepository: TodoRepository) : InsertTodo {

    override suspend operator fun invoke(todo: Todo): Long = todoRepository.insertTodo(todo)

}