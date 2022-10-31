package com.example.to_dolistclone.feature.tasks.domain.implementation.read

import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.tasks.domain.abstraction.read.GetTodoCategoriesWithTodos
import javax.inject.Inject

class GetTodoCategoriesWithTodosImpl @Inject constructor(private val todoRepository: TodoRepository) :
    GetTodoCategoriesWithTodos {

    override operator fun invoke() =
        todoRepository.getTodoCategoriesWithTodos()

}