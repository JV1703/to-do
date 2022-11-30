package com.example.to_dolistclone.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.feature.profile.domain.abstraction.ProfileTodoUseCase
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

data class ProfileUiState(
    val selectedDate: Long = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
        .toEpochMilli(),
    val todos: List<Todo> = emptyList(),
    val barGraphData: List<BarEntry> = emptyList(),
    val recyclerViewData: List<Todo> = emptyList(),
    val pieGraphData: List<Todo> = emptyList(),
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val todoUseCase: ProfileTodoUseCase, private val dateUtil: DateUtil
) : ViewModel() {

    private val selectedDate = MutableStateFlow(dateUtil.getCurrentDateTimeLong())
    private val todos = todoUseCase.todos

    init {
        getBarGraphData(dateUtil.getCurrentDateTimeLong())
    }

    private val pieGraphFilter = MutableStateFlow(PieGraphFilter.WEEK)

    init {
        getBarGraphData(dateUtil.getCurrentDateTimeLong())
    }

    private val barGraphData = selectedDate.flatMapLatest { date ->
        getBarGraphData(date)
    }
    private val recyclerViewData = getRecyclerViewData(dateUtil.getCurrentDateTimeLong())
    private val pieGraphData = pieGraphFilter.flatMapLatest { filter ->
        getPieGraphData(dateUtil.getCurrentDateTimeLong(), filter)
    }

    val uiState = combine(
        selectedDate, todos, barGraphData, recyclerViewData, pieGraphData
    ) { selectedDate, todos, barGraphData, recyclerViewData, pieGraphData ->
        ProfileUiState(
            selectedDate = selectedDate,
            todos = todos,
            barGraphData = barGraphData,
            recyclerViewData = recyclerViewData,
            pieGraphData = pieGraphData
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    private fun getBarGraphData(date: Long): Flow<List<BarEntry>> {

        val firstDateOfWeek = dateUtil.toLong(todoUseCase.getFirstDateOfWeek(date))
        val lastDateOfWeek = dateUtil.toLong(todoUseCase.getLastDateOfWeek(date))
        return todoUseCase.getCompletedTodosWithinTimeFrame(firstDateOfWeek, lastDateOfWeek)
            .map { todos ->
                val proxy = todos.groupingBy { it.completedOn }.eachCount()
                proxy.entries.mapIndexed { index, entry ->
                    BarEntry(index.toFloat(), entry.value.toFloat())
                }
            }
    }

    fun getBarGraphForTesting(date: Long): Flow<Map<Long, Int>> {
        val firstDateOfWeek = dateUtil.toLong(todoUseCase.getFirstDateOfWeek(date))
        val lastDateOfWeek = dateUtil.toLong(todoUseCase.getLastDateOfWeek(date))
        return todoUseCase.getCompletedTodosWithinTimeFrame(firstDateOfWeek, lastDateOfWeek)
            .map { todos ->
                todos.groupingBy { todo ->
                    todo.completedOn!!
                }.eachCount()
            }
    }

    private fun getPieGraphData(date: Long, timeFrame: PieGraphFilter): Flow<List<Todo>> {
        return todoUseCase.getTodosInTimeFrame(date, timeFrame)
    }

    private fun getRecyclerViewData(date: Long): Flow<List<Todo>> {
        val endDate = dateUtil.toLocalDate(date).plusWeeks(1)
        return todoUseCase.getTodosWithinTimeFrame(date, dateUtil.toLong(endDate))
    }

    private fun getFirstDayOfWeek(date: Long): LocalDate {
        return todoUseCase.getLastDateOfWeek(date)
    }

    private fun getLastDayOfWeek(date: Long): LocalDate {
        return todoUseCase.getLastDateOfWeek(date)
    }

    fun nextWeek(date: LocalDate): LocalDate {
        val updatedDate = date.plusWeeks(1)
        val firstDayOfWeek = getFirstDayOfWeek(dateUtil.toLong(updatedDate))
        selectedDate.value = dateUtil.toLong(firstDayOfWeek)
        return firstDayOfWeek
    }

    fun previousWeek(date: LocalDate): LocalDate {
        val updatedDate = date.minusWeeks(1)
        val firstDayOfWeek = getFirstDayOfWeek(dateUtil.toLong(updatedDate))
        selectedDate.value = dateUtil.toLong(firstDayOfWeek)
        return firstDayOfWeek
    }

    fun updatePieGraphFilter(filter: PieGraphFilter) {
        pieGraphFilter.value = filter
    }

}

enum class PieGraphFilter {
    WEEK, MONTH, ALL
}