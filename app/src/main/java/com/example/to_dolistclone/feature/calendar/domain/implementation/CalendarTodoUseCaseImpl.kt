package com.example.to_dolistclone.feature.calendar.domain.implementation

import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.calendar.domain.abstraction.CalendarTodoUseCase
import com.example.to_dolistclone.feature.common.domain.todo.BaseTodoUseCaseImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class CalendarTodoUseCaseImpl @Inject constructor(
    private val todoRepository: TodoRepository, private val dateUtil: DateUtil
) : BaseTodoUseCaseImpl(todoRepository), CalendarTodoUseCase {

    override fun getTodosGroupByDeadline(): Flow<Map<LocalDate?, List<Todo>>> {
        return todos.map { todos ->
            todos.groupBy { todo ->
                todo.deadline?.let { dateUtil.toLocalDate(it) }
            }
        }
    }

}