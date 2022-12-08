package com.example.to_dolistclone.feature.profile.domain.implementation

import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.common.domain.todo.BaseTodoUseCaseImpl
import com.example.to_dolistclone.feature.profile.domain.abstraction.ProfileTodoUseCase
import com.example.to_dolistclone.feature.profile.viewmodel.PieGraphFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class ProfileTodoUseCaseImpl @Inject constructor(
    private val todoRepository: TodoRepository, private val dateUtil: DateUtil
) : BaseTodoUseCaseImpl(todoRepository), ProfileTodoUseCase {

    override val todosSortedByCompletedOn =
        todos.map { todos -> todos.sortedBy { todo -> todo.completedOn } }

    override fun getTodosWithinTimeRange(todos: List<Todo>, from: Long, to: Long): List<Todo> =
        todos.filter { todo ->
            !todo.isComplete && isWithinDateRange(todo.deadline, from, to)
        }.sortedBy { it.deadline }

    override fun getCompletedTodosWithinTimeRange(
        todos: List<Todo>, from: Long, to: Long
    ): List<Todo> = todos.filter { todo ->
        todo.isComplete && isWithinDateRange(todo.completedOn, from, to)
    }.sortedBy { it.completedOn }

    override fun getTodosInTimeFrame(
        todos: List<Todo>,
        timeFrame: PieGraphFilter,
        date: LocalDate
    ): List<Todo> {
        val todayPlus7Days = date.plusDays(6)
        val todayPlus30Days = date.plusDays(29)

        return when (timeFrame) {

            PieGraphFilter.WEEK -> {
                val startDate = dateUtil.toLong(date)
                val endDate = dateUtil.toLong(todayPlus7Days)
                todos.filter { todo ->
                    isWithinDateRange(
                        todo.deadline, startDate, endDate
                    ) && !todo.isComplete
                }.sortedBy { it.deadline }
            }
            PieGraphFilter.MONTH -> {
                val startDate = dateUtil.toLong(date)
                val endDate = dateUtil.toLong(todayPlus30Days)
                todos.filter { todo ->
                    isWithinDateRange(
                        todo.deadline, startDate, endDate
                    ) && !todo.isComplete
                }.sortedBy { it.deadline }
            }
            PieGraphFilter.ALL -> {
                todos.filter { todo -> !todo.isComplete }.sortedBy { it.deadline }
            }
        }
    }

    override fun getSelectedPieGraphOption(): Flow<Int> = todoRepository.getSelectedPieGraphOption()

    override suspend fun saveSelectedPieGraphOption(selectedOption: Int) {
        todoRepository.saveSelectedPieGraphOption(selectedOption)
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