package com.example.to_dolistclone.feature.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {

    private val calendarType = MutableStateFlow(CalendarType.WEEK)
    private val selectedDate = MutableStateFlow<LocalDate?>(null)

    val uiState: StateFlow<UiState> = combine(calendarType, selectedDate) { type, date ->
        UiState(
            calendarType = type, selectedDate = date
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), UiState()
    )

    fun toggleCalendarType() {
        if (calendarType.value == CalendarType.WEEK) {
            calendarType.value = CalendarType.MONTH
        } else {
            calendarType.value = CalendarType.WEEK
        }
    }

    fun updateSelectedDate(date: LocalDate) {
        selectedDate.value = date
        Log.i("calendar_vm","selected date: ${selectedDate.value}")
    }

}

enum class CalendarType {
    WEEK, MONTH
}