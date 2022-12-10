package com.example.to_dolistclone.feature.todo.domain.implementation

import android.util.Log
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.common.worker.WorkerManager
import com.example.to_dolistclone.core.data.local.GenericCacheError.GENERIC_CACHE_ERROR
import com.example.to_dolistclone.core.data.local.handleCacheResponse
import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.common.domain.todo.BaseTodoUseCaseImpl
import com.example.to_dolistclone.feature.todo.domain.abstraction.TodoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TodoUseCaseImpl @Inject constructor(
    private val todoRepository: TodoRepository,
    private val dateUtil: DateUtil,
    private val workerManager: WorkerManager
) : BaseTodoUseCaseImpl(todoRepository), TodoUseCase {

    override suspend fun insertTodo(userId: String, todo: Todo): Async<Long?>{
        val cacheResult = todoRepository.insertTodo(todo)
        return handleCacheResponse(cacheResult){resultObj ->
            if(resultObj > 0){
                workerManager.upsertTodo(userId, todo.todoId)
                Async.Success(resultObj)
            }else{
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }

        }
    }

    override fun getCompletedTodos(): Flow<List<Todo>> = todos.map { todos ->
        todos.filter { it.isComplete && it.completedOn != null }.sortedBy { it.completedOn }
    }

    override suspend fun updateTodoCompletion(
        userId: String, todoId: String, isComplete: Boolean, completedOn: Long?
    ): Async<Int> {
        val cacheResult = todoRepository.updateTodoCompletion(todoId, isComplete, completedOn)
        return handleCacheResponse(cacheResult){resultObj ->
            if(resultObj>0){
                workerManager.upsertTodo(userId, todoId)
                Async.Success(resultObj)
            }else{
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    private val todoTimeCategory = listOf("previous", "today", "future", "completedToday")

    private fun getPreviousTodos(todos: List<Todo>, date: LocalDate): List<Todo> {
        val previous = todos.filter {
            !it.isComplete && it.deadline != null && dateUtil.toLocalDate(it.deadline)
                .isBefore(date)
        }
        Log.i("todos", "previous: $previous")
        return previous
    }

    private fun getTodayTodos(todos: List<Todo>, date: LocalDate): List<Todo> {
        val today = todos.filter {
            !it.isComplete && it.deadline != null && dateUtil.toLocalDate(it.deadline)
                .isEqual(date)
        }
        Log.i("todos", "today: $today")
        return today
    }

    private fun getFutureTodos(todos: List<Todo>, date: LocalDate): List<Todo> {
        val future = todos.filter {
            !it.isComplete && dateUtil.toLocalDate(
                it.deadline ?: dateUtil.toLong(date.plusDays(1))
            ).isAfter(date)
        }
        Log.i("todos", "future: $future")
        return future
    }

    private fun getCompletedTodayTodos(todos: List<Todo>, date: LocalDate): List<Todo> {
        val completedToday = todos.filter {
            it.isComplete && it.completedOn != null && dateUtil.toLocalDate(it.completedOn)
                .isEqual(date)
        }
        Log.i("todos", "completed today: $completedToday")
        return completedToday
    }

    override fun getMappedTodos(date: LocalDate): Flow<Map<String, List<Todo>>> {
        return todoRepository.getTodos().map { todos ->
            mapOf(
                todoTimeCategory[0] to getPreviousTodos(todos, date),
                todoTimeCategory[1] to getTodayTodos(todos, date),
                todoTimeCategory[2] to getFutureTodos(todos, date),
                todoTimeCategory[3] to getCompletedTodayTodos(todos, date)
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