package com.example.to_dolistclone.feature.calendar.misc

import android.view.View
import com.example.to_dolistclone.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.WeekDayPosition
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate

class WeekDayViewContainer(view: View, private val dateClicked: (WeekDay) -> Unit) :
    ViewContainer(view) {

    lateinit var day: WeekDay
    val binding = CalendarDayLayoutBinding.bind(view)

    init {
        view.setOnClickListener {
            if (day.position == WeekDayPosition.RangeDate) {
                dateClicked(day)
            }
        }
    }

}