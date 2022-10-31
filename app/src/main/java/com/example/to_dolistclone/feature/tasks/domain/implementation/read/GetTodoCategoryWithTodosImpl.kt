package com.example.to_dolistclone.feature.tasks.domain.implementation.read

import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.tasks.domain.abstraction.read.GetTodoCategoryWithTodos
import javax.inject.Inject

class GetTodoCategoryWithTodosImpl @Inject constructor(private val todoRepository: TodoRepository) :
    GetTodoCategoryWithTodos {

    override operator fun invoke(todoCategoryName: String) = todoRepository.getTodoCategoryWithTodos(todoCategoryName)

}