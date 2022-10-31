package com.example.to_dolistclone.feature.detail.domain.abstraction.delete

interface DeleteTask {
    suspend operator fun invoke(taskId: String): Int
}