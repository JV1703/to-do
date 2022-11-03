package com.example.to_dolistclone.feature.calendar

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.*
import androidx.fragment.app.viewModels
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.utils.ui.collectLatestLifecycleFlow
import com.example.to_dolistclone.core.utils.ui.makeToast
import com.example.to_dolistclone.databinding.FragmentCalendarBinding
import com.example.to_dolistclone.feature.BaseFragment
import com.example.to_dolistclone.feature.calendar.misc.MonthDayViewContainer
import com.example.to_dolistclone.feature.common.dialog.DialogsManager
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>(FragmentCalendarBinding::inflate) {

    @Inject
    lateinit var dialogsManager: DialogsManager

    private val viewModel: CalendarViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    private val today = LocalDate.now()
    private var selectedDate: LocalDate? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        val daysOfWeek = daysOfWeek()
        setupCalendar(startMonth, endMonth, currentMonth, daysOfWeek)
        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->
            selectedDate = uiState.selectedDate
            binding.monthCalendar.monthScrollListener = { updateTitle() }
        }
        binding.apply {
            nextMonth.setOnClickListener { scrollToNextMonth() }
            prevMonth.setOnClickListener { scrollToPrevMonth() }
        }
    }

    private fun setupCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {
        binding.legendLayout.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.text = daysOfWeek[index].displayText()
            }
        setupMonthCalendar(
            startMonth, endMonth, currentMonth, daysOfWeek
        )
    }

    private fun setupMonthCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>
    ) {
        binding.monthCalendar.dayBinder = monthBinder
        binding.monthCalendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.monthCalendar.scrollToMonth(currentMonth)
    }

    private val monthBinder = object : MonthDayBinder<MonthDayViewContainer> {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun bind(container: MonthDayViewContainer, data: CalendarDay) {
            container.day = data
            val dateTv = container.binding.dayTv
            val eventInd = container.binding.eventInd
            bindDate(data.date, dateTv, eventInd)
        }

        override fun create(view: View): MonthDayViewContainer = MonthDayViewContainer(view) {
            monthCalendarOnClickListener(it)
            makeToast(it.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindDate(date: LocalDate, textView: TextView, eventInd: View) {
        textView.text = date.dayOfMonth.toString()
        when (date) {
            today -> {
                textView.setBackgroundResource(R.drawable.calendar_today_bg)
                eventInd.makeInVisible()
            }
            selectedDate -> {
                textView.setBackgroundResource(R.drawable.calendar_selected_bg)
                eventInd.makeInVisible()
            }
            else -> {
                textView.background = null
                eventInd.isVisible = false
            }
        }
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.monthCalendar.notifyDateChanged(it) }
            binding.monthCalendar.notifyDateChanged(date)
            viewModel.updateSelectedDate(date)
        }
    }

    private fun monthCalendarOnClickListener(date: CalendarDay) {
        when (date.position) {
            DayPosition.InDate -> {
                scrollToPrevMonth()
                selectDate(date.date)
            }
            DayPosition.OutDate -> {
                scrollToNextMonth()
                selectDate(date.date)
            }
            DayPosition.MonthDate -> {
                selectDate(date.date)
            }
        }
    }

    private fun scrollToNextMonth() {
        binding.monthCalendar.findFirstVisibleMonth()?.yearMonth?.nextMonth?.let {
            binding.monthCalendar.scrollToMonth(it)
        }
    }

    private fun scrollToPrevMonth() {
        binding.monthCalendar.findFirstVisibleMonth()?.yearMonth?.previousMonth?.let {
            binding.monthCalendar.scrollToMonth(it)
        }
    }

    private fun updateTitle() {
        val month = binding.monthCalendar.findFirstVisibleMonth()?.yearMonth ?: return
        val monthText = month.month.displayText(short = false)
        val yearText = month.year.toString()
        binding.period.text = "$monthText $yearText"
    }
}

fun DayOfWeek.displayText(uppercase: Boolean = false): String {
    return getDisplayName(TextStyle.SHORT, Locale.ENGLISH).let { value ->
        if (uppercase) value.uppercase(Locale.ENGLISH) else value
    }
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.getDefault())
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}