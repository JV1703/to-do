package com.example.to_dolistclone.feature.profile.domain.abstraction

import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.feature.profile.viewmodel.PieGraphFilter
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ProfileTodoUseCase {

    val todos: Flow<List<Todo>>

    fun getTodosWithinTimeFrame(from: Long, to: Long): Flow<List<Todo>>

    fun getTodosInTimeFrame(timeFrame: PieGraphFilter): Flow<List<Todo>>

    fun getCompletedTodosWithinTimeFrame(from: Long, to: Long): Flow<List<Todo>>

    fun getSelectedPieGraphOption(): Flow<Int>

    suspend fun saveSelectedPieGraphOption(selectedOption: Int)
}