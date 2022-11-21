package com.example.to_dolistclone.feature.calendar

import com.example.to_dolistclone.core.domain.model.Todo
import java.time.LocalDate

data class CalendarFragmentUiState(
    val selectedDate: LocalDate? = null,
    val todos: Map<LocalDate?, List<Todo>> = emptyMap()
)