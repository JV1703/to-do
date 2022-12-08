package com.example.to_dolistclone.feature.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.feature.todo.domain.abstraction.TodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TodoFragmentUiState(
    val showPrevious: Boolean = true,
    val showToday: Boolean = true,
    val showFuture: Boolean = true,
    val showCompletedToday: Boolean = true,
    val previousTodos: List<Todo> = emptyList(),
    val todayTodos: List<Todo> = emptyList(),
    val futureTodos: List<Todo> = emptyList(),
    val completedTodayTodos: List<Todo> = emptyList()
)

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoUseCase: TodoUseCase, private val dateUtil: DateUtil
) : ViewModel() {

    private val mappedTodos = todoUseCase.getMappedTodos(dateUtil.getCurrentDate())
    private val showStatus = todoUseCase.getShowStatus()

    val todoFragmentUiState = combine(
        mappedTodos, showStatus
    ) { todos, showStatus ->
        TodoFragmentUiState(
            showPrevious = showStatus["previous"] ?: true,
            showToday = showStatus["today"] ?: true,
            showFuture = showStatus["future"] ?: true,
            showCompletedToday = showStatus["completedToday"] ?: true,
            previousTodos = todos["previous"] ?: emptyList(),
            todayTodos = todos["today"] ?: emptyList(),
            futureTodos = todos["future"] ?: emptyList(),
            completedTodayTodos = todos["completedToday"] ?: emptyList()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodoFragmentUiState()
    )

    fun updateTodoCompletion(todoId: String, isComplete: Boolean) {
        viewModelScope.launch {
            val currentDateTimeLong = dateUtil.getCurrentDateTimeLong()
            todoUseCase.updateTodoCompletion(
                todoId,
                isComplete = isComplete,
                completedOn = if (isComplete) currentDateTimeLong else null
            )
        }
    }

    fun saveShowPrevious(isShow: Boolean) {
        viewModelScope.launch {
            todoUseCase.saveShowPrevious(isShow)
        }
    }

    fun saveShowToday(isShow: Boolean) {
        viewModelScope.launch {
            todoUseCase.saveShowToday(isShow)
        }
    }

    fun saveShowFuture(isShow: Boolean) {
        viewModelScope.launch {
            todoUseCase.saveShowFuture(isShow)
        }
    }

    fun saveShowCompletedToday(isShow: Boolean) {
        viewModelScope.launch {
            todoUseCase.saveShowCompletedToday(isShow)
        }
    }

    fun saveSelectedTodoId(todoId: String) {
        viewModelScope.launch {
            todoUseCase.saveSelectedTodoId(todoId)
        }
    }
}