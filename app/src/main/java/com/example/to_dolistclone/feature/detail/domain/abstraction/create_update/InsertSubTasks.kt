package com.example.to_dolistclone.feature.detail.domain.abstraction.create_update

import com.example.to_dolistclone.core.domain.model.Task

interface InsertSubTasks{
    suspend operator fun invoke(tasks: List<Task>): LongArray
}