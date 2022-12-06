package com.example.to_dolistclone.feature.todo.adapter

import com.example.to_dolistclone.core.domain.model.Todo

interface TodosAdapterClickListener {

    fun navigate(todo: Todo)
    fun complete(todoId: String, isComplete: Boolean)

}