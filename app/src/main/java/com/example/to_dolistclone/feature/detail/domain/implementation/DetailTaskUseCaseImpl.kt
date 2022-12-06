package com.example.to_dolistclone.feature.detail.domain.implementation

import com.example.to_dolistclone.core.domain.model.Task
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.DetailTaskUseCase
import javax.inject.Inject

class DetailTaskUseCaseImpl @Inject constructor(private val todoRepository: TodoRepository) :
    DetailTaskUseCase {

    override suspend fun insertTasks(tasks: List<Task>): LongArray {
        val filteredTasks = tasks.filter {
            it.task.trim().isNotEmpty()
        }.mapIndexed { index, task ->
            task.copy(position = index)
        }

        return todoRepository.insertTasks(filteredTasks)
    }

    override suspend fun insertTask(task: Task): Long = todoRepository.insertTask(task)

    override suspend fun updateTaskPosition(taskId: String, position: Int): Int =
        todoRepository.updateTaskPosition(taskId, position)

    override suspend fun updateTaskTitle(taskId: String, title: String): Int =
        todoRepository.updateTaskTitle(
            taskId, title
        )

    override suspend fun updateTaskCompletion(taskId: String, isComplete: Boolean): Int =
        todoRepository.updateTaskCompletion(taskId, isComplete)

    override suspend fun deleteTask(taskId: String): Int = todoRepository.deleteTask(taskId)

}