package com.example.to_dolistclone.feature.todo.domain.abstraction

import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.Todo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TodoUseCase {

    suspend fun insertTodo(userId: String, todo: Todo): Async<Long?>

    suspend fun updateTodoCompletion(
        userId: String, todoId: String, isComplete: Boolean, completedOn: Long?, updatedOn: Long
    ): Async<Int>

//    fun getTodos(): Flow<List<Todo>>

    fun getMappedTodos(date: LocalDate): Flow<Map<String, List<Todo>>>

    fun getTodosGroupByDeadline(): Flow<Map<LocalDate?, List<Todo>>>

    suspend fun saveShowPrevious(isShow: Boolean)

    suspend fun saveShowToday(isShow: Boolean)

    suspend fun saveShowFuture(isShow: Boolean)

    suspend fun saveShowCompletedToday(isShow: Boolean)

    fun getShowStatus(): Flow<Map<String, Boolean>>

    fun getShowPrevious(): Flow<Boolean>

    fun getShowToday(): Flow<Boolean>

    fun getShowFuture(): Flow<Boolean>

    fun getShowCompletedToday(): Flow<Boolean>

    suspend fun saveSelectedTodoId(todoId: String)

    fun getCompletedTodos(): Flow<List<Todo>>
}