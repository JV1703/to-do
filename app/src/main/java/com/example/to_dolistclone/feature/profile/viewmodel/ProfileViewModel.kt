package com.example.to_dolistclone.feature.profile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.feature.profile.domain.abstraction.ProfileTodoUseCase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

data class ProfileUiState(
    val selectedDate: Long = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
        .toEpochMilli(),
    val todos: List<Todo> = emptyList(),
    val barChartData: List<BarEntry> = emptyList(),
    val recyclerViewData: List<Todo> = emptyList(),
    val pieChartData: List<PieEntry> = emptyList(),
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val todoUseCase: ProfileTodoUseCase, private val dateUtil: DateUtil
) : ViewModel() {

    private val selectedDate = MutableStateFlow(dateUtil.getCurrentDateTimeLong())
    private val todos = todoUseCase.todos

    val pieGraphFilter = todoUseCase.getSelectedPieGraphOption().map {
        when (it) {
            0 -> {
                PieGraphFilter.WEEK
            }
            1 -> {
                PieGraphFilter.MONTH
            }
            2 -> {
                PieGraphFilter.ALL
            }
            else -> throw IndexOutOfBoundsException("No options for pie graph filter")
        }
    }

    private val barGraphData = selectedDate.flatMapLatest { date ->
        getBarGraphData(date)
    }
    private val recyclerViewData = getRecyclerViewData(dateUtil.getCurrentDateTimeLong())
    private val pieGraphData = pieGraphFilter.flatMapLatest { filter ->
        getPieChartData(filter)
    }

    val uiState = combine(
        selectedDate, todos, barGraphData, recyclerViewData, pieGraphData
    ) { selectedDate, todos, barGraphData, recyclerViewData, pieGraphData ->
        Log.i("profileUiState", "triggered")
        ProfileUiState(
            selectedDate = selectedDate,
            todos = todos,
            barChartData = barGraphData,
            recyclerViewData = recyclerViewData,
            pieChartData = pieGraphData
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    private fun getBarGraphData(date: Long): Flow<List<BarEntry>> {
        val firstDateOfWeek = dateUtil.toLong(dateUtil.getFirstDateOfWeek(date))
        val lastDateOfWeek = dateUtil.toLong(dateUtil.getLastDateOfWeek(date))

        return todoUseCase.getCompletedTodosWithinTimeFrame(firstDateOfWeek, lastDateOfWeek)
            .map { todos ->
                val test = todos.groupingBy { dateUtil.toLocalDate(it.completedOn!!).dayOfWeek }
                    .eachCount()
                generateBarEntry(test)
            }
    }

    private fun generateBarEntry(data: Map<DayOfWeek, Int>): List<BarEntry> {
        val startDay = dateUtil.getFirstDayOfWeek()
        val daysInAWeek = dateUtil.generateDaysInWeek(startDay)
        val reference = daysInAWeek.associateWith {
            0
        }.toMutableMap()

        data.forEach { (dayOfWeek, i) ->
            reference[dayOfWeek] = i
        }

        val converted = reference.mapKeys {
            daysInAWeek.indexOf(it.key)
        }

        val output = converted.map {
            BarEntry(it.key.toFloat(), it.value.toFloat())
        }

        return output
    }

    private fun getPieChartData(timeFrame: PieGraphFilter): Flow<List<PieEntry>> {
        val data = todoUseCase.getTodosInTimeFrame(timeFrame)
        return data.map { todos ->
            todos.groupingBy { it.todoCategoryRefName }.eachCount().toMutableMap().mapKeys {
                "${it.key} (${it.value})"
            }.entries.map {
                PieEntry(it.value.toFloat(), it.key)
            }
        }
    }

    private fun getRecyclerViewData(date: Long): Flow<List<Todo>> {
        val startDate = dateUtil.toLocalDate(date).plusDays(1)
        val endDate = dateUtil.toLocalDate(date).plusWeeks(1)
        return todoUseCase.getTodosWithinTimeFrame(
            dateUtil.toLong(startDate), dateUtil.toLong(endDate)
        )
    }

    fun nextWeek(date: Long): LocalDate {
        val updatedDate = dateUtil.toLocalDate(date).plusWeeks(1)
        val firstDayOfWeek = dateUtil.getFirstDateOfWeek(updatedDate)
        selectedDate.value = dateUtil.toLong(firstDayOfWeek)
        return firstDayOfWeek
    }

    fun previousWeek(date: Long): LocalDate {
        val updatedDate = dateUtil.toLocalDate(date).minusWeeks(1)
        val firstDayOfWeek = dateUtil.getFirstDateOfWeek(updatedDate)
        selectedDate.value = dateUtil.toLong(firstDayOfWeek)
        return firstDayOfWeek
    }

    fun updatePieGraphFilter(filter: PieGraphFilter) {
        viewModelScope.launch {
            todoUseCase.saveSelectedPieGraphOption(filter.toInt())
        }
    }

}

enum class PieGraphFilter {
    WEEK, MONTH, ALL
}

fun PieGraphFilter.toInt(): Int {
    return when (this) {
        PieGraphFilter.WEEK -> 0
        PieGraphFilter.MONTH -> 1
        PieGraphFilter.ALL -> 2
    }
}