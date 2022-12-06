package com.example.to_dolistclone.feature.todo.adapter

import com.example.to_dolistclone.core.domain.model.Todo

interface CompletedTodosAdapterClickListener {

    fun navigate(todo: Todo)
    fun complete(todoId: String, isComplete: Boolean)

}