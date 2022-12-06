package com.example.to_dolistclone.feature.todo_shortcut.domain.implementation

import com.example.to_dolistclone.core.domain.model.Task
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.domain.model.TodoCategory
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import javax.inject.Inject

class TodoShortcutUseCaseImpl @Inject constructor(private val todoRepository: TodoRepository) {

    suspend fun insertTodo(todo: Todo) = todoRepository.insertTodo(todo)

    suspend fun insertTasks(tasks: List<Task>): LongArray {
        val filteredTasks = tasks.filter {
            it.task.trim().isNotEmpty()
        }.mapIndexed { index, task ->
            task.copy(position = index)
        }
        return todoRepository.insertTasks(filteredTasks)
    }

    suspend fun insertTodoCategory(categoryName: String): Long =
        todoRepository.insertTodoCategory(TodoCategory(categoryName))

    fun getTodoCategories() = todoRepository.getTodoCategories()

}