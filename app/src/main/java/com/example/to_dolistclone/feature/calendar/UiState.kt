package com.example.to_dolistclone.feature.calendar

import java.time.LocalDate

data class UiState(
    val calendarType: CalendarType = CalendarType.WEEK,
    val selectedDate: LocalDate? = null
)