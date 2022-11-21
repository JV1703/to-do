package com.example.to_dolistclone.feature.common.dialog.modal_bottom_sheet

import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.feature.home.adapter.TaskProxy

interface TodoShortcutListener {

    fun insertTodoCategory(categoryName: String)
    fun updateSelectedCategory(categoryName: String)
    fun insertTodo(tasksProxy: List<TaskProxy>, todo: Todo)

}