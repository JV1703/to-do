package com.example.to_dolistclone.feature.detail.domain.abstraction.delete

interface DeleteTodo {
    suspend operator fun invoke(todoId: String): Int
}