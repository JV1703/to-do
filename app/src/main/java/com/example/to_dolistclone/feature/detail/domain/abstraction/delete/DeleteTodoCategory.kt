package com.example.to_dolistclone.feature.detail.domain.abstraction.delete

interface DeleteTodoCategory {
    suspend operator fun invoke(todoCategoryName: String): Int
}