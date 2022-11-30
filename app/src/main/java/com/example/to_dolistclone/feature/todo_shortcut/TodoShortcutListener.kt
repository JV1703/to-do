package com.example.to_dolistclone.feature.todo_shortcut

import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.domain.model.TodoCategory
import com.example.to_dolistclone.feature.home.adapter.TaskProxy

interface TodoShortcutListener {

    fun loadTodoCategories(): Set<String>
    fun addTodoCategory(categoryName: String): Long
    fun updateSelectedCategory(categoryName: String): Int
    fun insertTodo(tasksProxy: List<TaskProxy>, todo: Todo): Int

}