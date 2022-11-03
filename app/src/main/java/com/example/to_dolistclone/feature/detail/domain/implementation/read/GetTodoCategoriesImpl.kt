package com.example.to_dolistclone.feature.detail.domain.implementation.read

import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.read.GetTodoCategories
import javax.inject.Inject

class GetTodoCategoriesImpl @Inject constructor(private val todoRepository: TodoRepository) :
    GetTodoCategories {

    override operator fun invoke() = todoRepository.getTodoCategories()

}