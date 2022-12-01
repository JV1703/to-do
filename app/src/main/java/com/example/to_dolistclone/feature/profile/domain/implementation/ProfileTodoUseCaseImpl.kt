package com.example.to_dolistclone.feature.profile.domain.implementation

import android.util.Log
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.profile.domain.abstraction.ProfileTodoUseCase
import com.example.to_dolistclone.feature.profile.viewmodel.PieGraphFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class ProfileTodoUseCaseImpl @Inject constructor(
    private val todoRepository: TodoRepository, private val dateUtil: DateUtil
) : ProfileTodoUseCase {

    override val todos = todoRepository.getTodos().map { todos ->
        Log.i("ProfileTodoUseCaseImpl", "todos: ${todos.size}")
        todos.sortedBy { todo ->
            todo.completedOn
        }
    }

    override fun getTodosWithinTimeFrame(from: Long, to: Long): Flow<List<Todo>> =
        todos.map { todos ->
            todos.filter { todo ->
                !todo.isComplete && isWithinDateRange(todo.deadline, from, to)
            }
        }.catch { e ->
            Log.e("ProfileTodoUseCaseImpl", "getTodosWithinTimeFrame: ${e.message}")
        }

    override fun getCompletedTodosWithinTimeFrame(from: Long, to: Long): Flow<List<Todo>> =
        todos.map { list ->
            list.filter { todo ->
                todo.isComplete && isWithinDateRange(todo.completedOn, from, to)
            }
        }.catch { e ->
            Log.e(
                "ProfileTodoUseCaseImpl",
                "getCompletedTodosWithinTimeFrame - errorMsg: ${e.message}"
            )
        }

    fun getTodosWithinTheWeek(date: Long): Flow<List<Todo>> = todos.map { todos ->
        val firstDayOfWeek = dateUtil.getFirstDateOfWeek(date)
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

    override fun getTodosInTimeFrame(timeFrame: PieGraphFilter): Flow<List<Todo>> =
        todos.map { todos ->
            val today = dateUtil.getCurrentDate().plusDays(1)
            val lastDateOfWeek = today.plusDays(7)
            val lastDateOfMonth = today.plusDays(30)

            when (timeFrame) {

                PieGraphFilter.WEEK -> {
                    val startDate = dateUtil.toLong(today)
                    val endDate = dateUtil.toLong(lastDateOfWeek)
                    todos.filter { todo -> isWithinDateRange(todo.deadline, startDate, endDate) && !todo.isComplete }
                }
                PieGraphFilter.MONTH -> {
                    val startDate = dateUtil.toLong(today)
                    val endDate = dateUtil.toLong(lastDateOfMonth)
                    todos.filter { todo -> isWithinDateRange(todo.deadline, startDate, endDate) && !todo.isComplete}
                }
                PieGraphFilter.ALL -> {
                    todos.filter{todo -> !todo.isComplete}
                }
            }
        }.catch { e ->
            Log.e("ProfileTodoUseCaseImpl", "getTodosInTimeFrame: ${e.message}")
        }

    private fun isWithinDateRange(date: Long?, from: Long, to: Long): Boolean {
        return if (date == null) {
            false
        } else {
            val currentDate = dateUtil.toLocalDate(date)
            val start = dateUtil.toLocalDate(from).minusDays(1)
            val end = dateUtil.toLocalDate(to).plusDays(1)
            Log.i(
                "ProfileTodoUseCaseImpl",
                "isWithinDateRange - date: ${dateUtil.toLocalDate(date)}. start: ${start}, end: ${end}, isWithinDateRange: ${
                    currentDate.isAfter(start) && currentDate.isBefore(end)
                }"
            )
            currentDate.isAfter(start) && currentDate.isBefore(end)
        }
    }

    override fun getSelectedPieGraphOption(): Flow<Int> = todoRepository.getSelectedPieGraphOption()

    override suspend fun saveSelectedPieGraphOption(selectedOption: Int) {
        todoRepository.saveSelectedPieGraphOption(selectedOption)
    }
}