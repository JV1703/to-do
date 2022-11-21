package com.example.to_dolistclone.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolistclone.feature.todo.domain.implementation.TodoUseCaseImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    taskTodoUseCase: TodoUseCaseImpl
) : ViewModel() {

    private val selectedDate = MutableStateFlow<LocalDate?>(null)
    private val mappedTodos = taskTodoUseCase.getTodosGroupByDeadline()

    val calendarFragmentUiState: StateFlow<CalendarFragmentUiState> =
        combine(selectedDate, mappedTodos) { date, mappedTodos ->
            CalendarFragmentUiState(
                selectedDate = date, todos = mappedTodos
            )
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarFragmentUiState()
        )

}