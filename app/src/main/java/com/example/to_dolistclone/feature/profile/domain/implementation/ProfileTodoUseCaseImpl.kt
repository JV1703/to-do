package com.example.to_dolistclone.feature.profile.domain.implementation

import android.util.Log
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.profile.domain.abstraction.ProfileTodoUseCase
import com.example.to_dolistclone.feature.profile.viewmodel.PieGraphFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject

class ProfileTodoUseCaseImpl @Inject constructor(
    private val todoRepository: TodoRepository, private val dateUtil: DateUtil
) : ProfileTodoUseCase {

    override val todos = todoRepository.getTodos().map { todos ->
        todos.sortedBy { todo ->
            todo.completedOn
        }
    }

    override fun getTodosWithinTimeFrame(from: Long, to: Long): Flow<List<Todo>> =
        todos.map { todos ->
            todos.filter { todo ->
                Log.i(
                    "ProfileTodoUseCaseImpl", "todoCompletedOn: ${
                        dateUtil.toString(
                            todo.completedOn!!, "EEE, MMM dd, yyyy", Locale.getDefault()
                        )
                    }, " + "from: ${
                        dateUtil.toString(
                            from, "EEE, MMM dd, yyyy", Locale.getDefault()
                        )
                    }, to: ${
                        dateUtil.toString(
                            to, "EEE, MMM dd, yyyy", Locale.getDefault()
                        )
                    }, test: ${isWithinDateRange(todo.completedOn, from, to)}"
                )
                isWithinDateRange(todo.completedOn, from, to)
            }
        }

    override fun getCompletedTodosWithinTimeFrame(from: Long, to: Long): Flow<List<Todo>> =
        todos.map { list ->
            list.filter { todo ->
                todo.isComplete && isWithinDateRange(todo.completedOn, from, to)
            }
        }

    fun getTodosWithinTheWeek(date: Long): Flow<List<Todo>> = todos.map { todos ->
        val firstDayOfWeek = getFirstDateOfWeek(date)
        val lastDayOfWeek = firstDayOfWeek.plusDays(6)
        todos.filter { todo ->
            Log.i(
                "ProfileTodoUseCaseImpl", "todoCompletedOn: ${
                    dateUtil.toString(
                        todo.completedOn!!, "EEE, MMM dd, yyyy", Locale.getDefault()
                    )
                }, " + "from: ${
                    dateUtil.toString(
                        firstDayOfWeek!!, "EEE, MMM dd, yyyy", Locale.getDefault()
                    )
                }, to: ${
                    dateUtil.toString(
                        lastDayOfWeek!!, "EEE, MMM dd, yyyy", Locale.getDefault()
                    )
                }"
            )
            isWithinDateRange(
                todo.completedOn, dateUtil.toLong(firstDayOfWeek), dateUtil.toLong(lastDayOfWeek)
            )
        }
    }

    override fun getTodosInTimeFrame(date: Long, timeFrame: PieGraphFilter) = todos.map { todos ->
        when (timeFrame) {
            PieGraphFilter.WEEK -> {
                val startDate = dateUtil.toLong(getFirstDateOfWeek(date))
                val endDate = dateUtil.toLong(getLastDateOfWeek(date))
                todos.filter { todo -> isWithinDateRange(todo.completedOn, startDate, endDate) }
            }
            PieGraphFilter.MONTH -> {
                val startDate = dateUtil.toLong(getFirstDayOfMonth(date))
                val endDate = dateUtil.toLong(getLastDayOfMonth(date))
                todos.filter { todo -> isWithinDateRange(todo.completedOn, startDate, endDate) }
            }
            PieGraphFilter.ALL -> {
                todos
            }
        }
    }

    override fun getFirstDateOfWeek(date: Long): LocalDate {
        val currentDate = dateUtil.toLocalDate(date)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        return currentDate.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    }

    override fun getLastDateOfWeek(date: Long): LocalDate {
        val currentDate = dateUtil.toLocalDate(date)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val lastDayOfWeek = DayOfWeek.of(((firstDayOfWeek.value + 5) % DayOfWeek.values().size) + 1)
        return currentDate.with(TemporalAdjusters.nextOrSame(lastDayOfWeek))
    }

    override fun getFirstDayOfMonth(date: Long): LocalDate {
        val selectedDate = dateUtil.toLocalDate(date)
        return selectedDate.withDayOfMonth(1)
    }

    override fun getLastDayOfMonth(date: Long): LocalDate {
        val selectedDate = getFirstDayOfMonth(date)
        val isLeapYear = selectedDate.isLeapYear
        val lengthOfMonth = selectedDate.month.length(isLeapYear)
        return selectedDate.withDayOfMonth(lengthOfMonth)
    }

    private fun isWithinDateRange(date: Long?, from: Long, to: Long): Boolean {
        return if (date == null) {
            false
        } else {
            val currentDate = dateUtil.toLocalDate(date)
            val start = dateUtil.toLocalDate(from).minusDays(1)
            val end = dateUtil.toLocalDate(to).plusDays(1)

            currentDate.isAfter(start) && currentDate.isBefore(end)
        }
    }

}