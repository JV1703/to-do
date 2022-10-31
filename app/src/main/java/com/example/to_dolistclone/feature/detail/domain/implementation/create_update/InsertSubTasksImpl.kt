package com.example.to_dolistclone.feature.detail.domain.implementation.create_update

import com.example.to_dolistclone.core.domain.model.Task
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.create_update.InsertSubTasks
import javax.inject.Inject

class InsertSubTasksImpl @Inject constructor(private val todoRepository: TodoRepository) :
    InsertSubTasks {

    override suspend operator fun invoke(tasks: List<Task>): LongArray = todoRepository.insertTasks(tasks)

}