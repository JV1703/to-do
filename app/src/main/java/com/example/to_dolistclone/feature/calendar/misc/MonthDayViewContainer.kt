package com.example.to_dolistclone.feature.calendar.misc

import android.view.View
import com.example.to_dolistclone.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate

class MonthDayViewContainer(view: View, private val selectDate: (LocalDate) -> Unit) :
    ViewContainer(view) {

    lateinit var day: CalendarDay
    val binding = CalendarDayLayoutBinding.bind(view)

    init {
        view.setOnClickListener {
            selectDate(day.date)
        }
    }

}