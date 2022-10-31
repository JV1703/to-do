package com.example.to_dolistclone.feature.detail.domain.implementation.delete

import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.delete.DeleteTask
import javax.inject.Inject

class DeleteTaskImpl @Inject constructor(private val todoRepository: TodoRepository) : DeleteTask {

    override suspend operator fun invoke(taskId: String): Int = todoRepository.deleteTask(taskId)

}