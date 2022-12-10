package com.example.to_dolistclone.feature.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.feature.todo.adapter.CompletedTodosHelper
import com.example.to_dolistclone.feature.todo.domain.implementation.TodoUseCaseImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompletedTodoViewModel @Inject constructor(
    private val todoUseCase: TodoUseCaseImpl,
    private val dateUtil: DateUtil
) : ViewModel() {

    private val completedTodos = todoUseCase.getCompletedTodos()
    val completedTodosList = completedTodos.map { todos ->

        val output = arrayListOf<CompletedTodosHelper>()

        todos.map { dateUtil.toLocalDate(it.completedOn!!) }
            .distinct()
            .forEachIndexed { index, date ->
                if (index == 0) {
                    output.add(CompletedTodosHelper.HeaderStart(date))
                } else {
                    output.add(CompletedTodosHelper.Header(date))
                }

                todos.filter { dateUtil.toLocalDate(it.completedOn!!) == date }
                    .forEach { todo ->
                        output.add(CompletedTodosHelper.CompletedTodos(todo))
                    }
            }

        output.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun saveSelectedTodoId(todoId: String) {
        viewModelScope.launch {
            todoUseCase.saveSelectedTodoId(todoId)
        }
    }

    fun updateTodoCompletion(userId: String, todoId: String, isComplete: Boolean, updatedOn: Long = dateUtil.getCurrentDateTimeLong()) {
        viewModelScope.launch {
            val currentDateTimeLong = dateUtil.getCurrentDateTimeLong()
            todoUseCase.updateTodoCompletion(
                userId = userId,
                todoId = todoId,
                isComplete = isComplete,
                completedOn = if (isComplete) currentDateTimeLong else null,
                updatedOn = updatedOn
            )
        }
    }

}