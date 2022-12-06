package com.example.to_dolistclone.feature.todo.adapter

import com.example.to_dolistclone.core.domain.model.Todo
import java.time.LocalDate

sealed class CompletedTodosHelper {
    data class HeaderStart(val completedOn: LocalDate): CompletedTodosHelper()
    data class Header(val completedOn: LocalDate): CompletedTodosHelper()
    data class CompletedTodos(val todo: Todo): CompletedTodosHelper()
}

enum class TimelineViewType(val viewType: Int){
    VIEW_TYPE_HEADER_START(0),
    VIEW_TYPE_HEADER(1),
    VIEW_TYPE_CONTENT(2),
}