package com.example.to_dolistclone.feature.todo.domain.implementation

import android.util.Log
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.todo.domain.abstraction.TodoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class TodoUseCaseImpl @Inject constructor(
    private val todoRepository: TodoRepository,
    private val dateUtil: DateUtil
) : TodoUseCase {

    override suspend fun insertTodo(todo: Todo): Long = todoRepository.insertTodo(todo)

    override suspend fun updateTodoCompletion(
        todoId: String, isComplete: Boolean, completedOn: Long?
    ): Int = todoRepository.updateTodoCompletion(todoId, isComplete, completedOn)

    private val currentDate =
        dateUtil.getZonedDateTime(dateTime = LocalDateTime.now()).toLocalDate()
    private val todoTimeCategory = listOf("previous", "today", "future", "completedToday")

    private fun getPreviousTodos(todos: List<Todo>): List<Todo> {
        val previous = todos.filter {
            !it.isComplete && it.deadline != null && dateUtil.toLocalDate(it.deadline)
                .isBefore(currentDate)
        }
        Log.i("todos", "previous: $previous")
        return previous
    }

    private fun getTodayTodos(todos: List<Todo>): List<Todo> {
        val today = todos.filter {
            !it.isComplete && it.deadline != null && dateUtil.toLocalDate(it.deadline)
                .isEqual(currentDate)
        }
        Log.i("todos", "today: $today")
        return today
    }

    private fun getFutureTodos(todos: List<Todo>): List<Todo> {
        val future = todos.filter {
            !it.isComplete && dateUtil.toLocalDate(
                it.deadline ?: dateUtil.toLong(currentDate.plusDays(1))
            ).isAfter(currentDate)
        }
        Log.i("todos", "future: $future")
        return future
    }

    private fun getCompletedTodayTodos(todos: List<Todo>): List<Todo> {
        val completedToday = todos.filter {
            it.isComplete && it.completedOn != null && dateUtil.toLocalDate(it.completedOn)
                .isEqual(currentDate)
        }
        Log.i("todos", "completed today: $completedToday")
        return completedToday
    }

    override fun getTodos(): Flow<List<Todo>> = todoRepository.getTodos()

    override fun getMappedTodos(): Flow<Map<String, List<Todo>>> {
        return todoRepository.getTodos().map { todos ->
            mapOf(
                todoTimeCategory[0] to getPreviousTodos(todos),
                todoTimeCategory[1] to getTodayTodos(todos),
                todoTimeCategory[2] to getFutureTodos(todos),
                todoTimeCategory[3] to getCompletedTodayTodos(todos)
            )
        }
    }

    override fun getTodosGroupByDeadline(): Flow<Map<LocalDate?, List<Todo>>> {
        return todoRepository.getTodos().map { todos ->
            todos.groupBy { todo ->
                todo.deadline?.let { dateUtil.toLocalDate(it) }
            }
        }
    }

    override suspend fun saveShowPrevious(isShow: Boolean) {
        Log.i("getTodos", "saveShowPrevious: ran")
        todoRepository.saveShowPrevious(isShow)
    }

    override suspend fun saveShowToday(isShow: Boolean) {
        Log.i("getTodos", "saveShowToday: ran")
        todoRepository.saveShowToday(isShow)
    }

    override suspend fun saveShowFuture(isShow: Boolean) {
        Log.i("getTodos", "saveShowFuture: ran")
        todoRepository.saveShowFuture(isShow)
    }

    override suspend fun saveShowCompletedToday(isShow: Boolean) {
        Log.i("getTodos", "saveShowCompletedToday: ran")
        todoRepository.saveShowCompletedToday(isShow)
    }

    override fun getShowStatus(): Flow<Map<String, Boolean>> {
        return combine(
            getShowPrevious(),
            getShowToday(),
            getShowFuture(),
            getShowCompletedToday()
        ) { previous, today, future, completedToday ->
            mapOf(
                todoTimeCategory[0] to previous,
                todoTimeCategory[1] to today,
                todoTimeCategory[2] to future,
                todoTimeCategory[3] to completedToday
            )
        }
    }

    override fun getShowPrevious(): Flow<Boolean> {
        Log.i("getTodos", "showPrevious: ran")
        return todoRepository.getShowPrevious()
    }

    override fun getShowToday(): Flow<Boolean> {
        Log.i("getTodos", "showToday: ran")
        return todoRepository.getShowToday()
    }

    override fun getShowFuture(): Flow<Boolean> {
        Log.i("getTodos", "showFuture: ran")
        return todoRepository.getShowFuture()
    }

    override fun getShowCompletedToday(): Flow<Boolean> {
        Log.i("getTodos", "showCompletedToday: ran")
        return todoRepository.getShowCompletedToday()
    }

}