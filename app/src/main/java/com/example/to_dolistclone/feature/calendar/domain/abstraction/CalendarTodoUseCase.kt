package com.example.to_dolistclone.feature.calendar.domain.abstraction

import com.example.to_dolistclone.core.domain.model.Todo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CalendarTodoUseCase {
    val todos: Flow<List<Todo>>
    fun getTodosGroupByDeadline(): Flow<Map<LocalDate?, List<Todo>>>
    suspend fun saveSelectedTodoId(todoId: String)
}