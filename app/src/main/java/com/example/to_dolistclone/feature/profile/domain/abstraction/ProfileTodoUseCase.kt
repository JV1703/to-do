package com.example.to_dolistclone.feature.profile.domain.abstraction

import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.feature.profile.viewmodel.PieGraphFilter
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

interface ProfileTodoUseCase {

    val todosSortedByCompletedOn: Flow<List<Todo>>

    fun getSelectedPieGraphOption(): Flow<Int>

    suspend fun saveSelectedPieGraphOption(selectedOption: Int)

    fun getTodosWithinTimeRange(todos: List<Todo>, from: Long, to: Long): List<Todo>

    fun getTodosInTimeFrame(todos: List<Todo>, timeFrame: PieGraphFilter, date: LocalDate): List<Todo>

    fun getCompletedTodosWithinTimeRange(todos: List<Todo>, from: Long, to: Long): List<Todo>

}