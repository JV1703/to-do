package com.example.to_dolistclone.feature.profile.domain.abstraction

import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.feature.profile.viewmodel.PieGraphFilter
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ProfileTodoUseCase {

    val todos: Flow<List<Todo>>

    fun getTodosWithinTimeFrame(from: Long, to: Long): Flow<List<Todo>>

    fun getFirstDateOfWeek(date: Long): LocalDate
    fun getLastDateOfWeek(date: Long): LocalDate
    fun getFirstDayOfMonth(date: Long): LocalDate
    fun getLastDayOfMonth(date: Long): LocalDate
    fun getTodosInTimeFrame(date: Long, timeFrame: PieGraphFilter): Flow<List<Todo>>
    fun getCompletedTodosWithinTimeFrame(from: Long, to: Long): Flow<List<Todo>>
}