package com.example.to_dolistclone.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolistclone.feature.calendar.domain.abstraction.CalendarTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val todoUseCase: CalendarTodoUseCase
) : ViewModel() {

    private val selectedDate = MutableStateFlow<LocalDate?>(null)
    private val mappedTodos = todoUseCase.getTodosGroupByDeadline()

    val calendarFragmentUiState: StateFlow<CalendarFragmentUiState> =
        combine(selectedDate, mappedTodos) { date, mappedTodos ->
            CalendarFragmentUiState(
                selectedDate = date, todos = mappedTodos
            )
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarFragmentUiState()
        )

    fun saveSelectedTodoId(todoId: String) {
        viewModelScope.launch {
            todoUseCase.saveSelectedTodoId(todoId)
        }
    }
}