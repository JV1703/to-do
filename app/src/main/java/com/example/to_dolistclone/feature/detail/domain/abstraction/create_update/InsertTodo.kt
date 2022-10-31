package com.example.to_dolistclone.feature.detail.domain.abstraction.create_update

import com.example.to_dolistclone.core.domain.model.Todo

interface InsertTodo {
    suspend operator fun invoke(todo: Todo): Long
}