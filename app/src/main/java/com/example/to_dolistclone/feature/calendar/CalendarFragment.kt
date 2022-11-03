package com.example.to_dolistclone.feature.calendar

import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnStart
import androidx.core.view.*
import androidx.fragment.app.viewModels
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.utils.ui.collectLatestLifecycleFlow
import com.example.to_dolistclone.databinding.FragmentCalendarBinding
import com.example.to_dolistclone.feature.BaseFragment
import com.example.to_dolistclone.feature.calendar.misc.MonthDayViewContainer
import com.example.to_dolistclone.feature.calendar.misc.WeekDayViewContainer
import com.example.to_dolistclone.feature.common.dialog.DialogsManager
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.WeekDayBinder
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
            val calendarType = uiState.calendarType
            selectedDate = uiState.selectedDate
            binding.weekCalendar.isGone = calendarType == CalendarType.MONTH
            binding.monthCalendar.isGone = calendarType == CalendarType.WEEK
            binding.weekCalendar.weekScrollListener = { updateTitle(calendarType) }
            binding.monthCalendar.monthScrollListener = { updateTitle(calendarType) }
            updateCalendarDetails(calendarType)
            binding.calendarToggle.setOnClickListener{
                viewModel.toggleCalendarType()
                toggleCalendar(calendarType)
            }
        }

    }

    private fun setupCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {
        setupMonthCalendar(
            startMonth, endMonth, currentMonth, daysOfWeek
        )
        setupWeekCalendar(
            startMonth, endMonth, currentMonth, daysOfWeek
        )
    }

    private fun setupMonthCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {

        binding.monthCalendar.dayBinder = object : MonthDayBinder<MonthDayViewContainer> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun bind(container: MonthDayViewContainer, data: CalendarDay) {
                container.day = data
                bindDate(
                    data.date, container.binding.dayTv, container.binding.eventInd
                )
            }

            override fun create(view: View): MonthDayViewContainer =
                MonthDayViewContainer(view) { day ->
                    val month = binding.monthCalendar.findFirstVisibleMonth()?.yearMonth
                    if (day.position == DayPosition.MonthDate) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            oldDate?.let { date ->
                                binding.monthCalendar.notifyDateChanged(date)
                            }
                            binding.monthCalendar.notifyDateChanged(day.date)
                            viewModel.updateSelectedDate(day.date)
                        }
                    } else if (day.position == DayPosition.InDate) {
                        month?.let {
                            binding.monthCalendar.smoothScrollToMonth(it.minusMonths(1))
                            if (selectedDate != day.date) {
                                val oldDate = selectedDate
                                selectedDate = day.date
                                oldDate?.let { date ->
                                    binding.monthCalendar.notifyDateChanged(date)
                                }
                                binding.monthCalendar.notifyDateChanged(day.date)
                                viewModel.updateSelectedDate(day.date)
                            }
                        }
                    } else if (day.position == DayPosition.OutDate) {
                        month?.let {
                            binding.monthCalendar.smoothScrollToMonth(it.plusMonths(1))
                            if (selectedDate != day.date) {
                                val oldDate = selectedDate
                                selectedDate = day.date
                                oldDate?.let { date ->
                                    binding.monthCalendar.notifyDateChanged(date)
                                }
                                binding.monthCalendar.notifyDateChanged(day.date)
                                viewModel.updateSelectedDate(day.date)
                            }
                        }
                    }
                }

        }

        binding.monthCalendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.monthCalendar.scrollToMonth(currentMonth)

    }

    private fun setupWeekCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {

        binding.weekCalendar.dayBinder = object : WeekDayBinder<WeekDayViewContainer> {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun bind(container: WeekDayViewContainer, data: WeekDay) {
                container.day = data
                bindDate(
                    data.date, container.binding.dayTv, container.binding.eventInd
                )
            }

            override fun create(view: View): WeekDayViewContainer = WeekDayViewContainer(view) {
                if (selectedDate != it) {
                    val oldDate = selectedDate
                    selectedDate = it
                    oldDate?.let { date ->
                        binding.weekCalendar.notifyDateChanged(date)
                    }
                    binding.weekCalendar.notifyDateChanged(it)
                    viewModel.updateSelectedDate(it)
                }

                Log.i(
                    "calendar_week",
                    "dayOfMonth: ${it.dayOfMonth} month: ${it.month} dayOfWeek: ${it.dayOfWeek} dayOfYear: ${it.dayOfYear} year: ${it.year} monthValue: ${it.monthValue} is selected"
                )
            }
        }

        binding.weekCalendar.setup(
            startMonth.atStartOfMonth(),
            endMonth.atEndOfMonth(),
            daysOfWeek.first(),
        )
        binding.weekCalendar.scrollToWeek(currentMonth.atStartOfMonth())

    }

    private fun updateTitle(calendarType: CalendarType) {
        val isMonthMode = calendarType != CalendarType.WEEK
        if (isMonthMode) {
            val month = binding.monthCalendar.findFirstVisibleMonth()?.yearMonth ?: return
            binding.period.text = "${month.month.displayText(short = false)} ${month.year}"
        } else {
            val week = binding.weekCalendar.findFirstVisibleWeek() ?: return
            // In week mode, we show the header a bit differently because
            // an index can contain dates from different months/years.
            val firstDate = week.days.first().date
            val lastDate = week.days.last().date
            if (firstDate.yearMonth == lastDate.yearMonth) {
                binding.period.text =
                    "${firstDate.month.displayText(short = false)} ${firstDate.year}"
            } else {
                val monthText = "${firstDate.month.displayText(short = false)} - ${
                    lastDate.month.displayText(short = false)
                }"
                val yearText = if (firstDate.year == lastDate.year) {
                    firstDate.year.toString()
                } else {
                    "${firstDate.year} - ${lastDate.year}"
                }
                binding.period.text = "$monthText $yearText"
            }
        }
    }

    private fun updateCalendarDetails(calendarType: CalendarType) {
        when (calendarType) {
            CalendarType.WEEK -> {
                binding.weekCalendar.weekScrollListener = { updateTitle(calendarType) }
            }
            CalendarType.MONTH -> {
                binding.monthCalendar.monthScrollListener = { updateTitle(calendarType) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindDate(
        date: LocalDate, textView: TextView, eventInd: View
    ) {
        textView.text = date.dayOfMonth.toString()
        when (date) {
            today -> {
                textView.setBackgroundResource(R.drawable.calendar_today_bg)
                eventInd.makeInVisible()
            }
            selectedDate -> {
//                            textView.setTextColorRes(R.color.example_3_blue)
                textView.setBackgroundResource(R.drawable.calendar_selected_bg)
                eventInd.makeInVisible()
            }
            else -> {
//                            textView.setTextColorRes(R.color.example_3_black)
                textView.background = null
                // boolean check if there is event. events is list of events
//                eventInd.isVisible = events[data.date].orEmpty().isNotEmpty()
                eventInd.isVisible = false
            }
        }
    }

    private fun toggleCalendar(calendarType: CalendarType) {
        if (calendarType == CalendarType.WEEK) {
            val targetDate = binding.monthCalendar.findFirstVisibleDay()?.date ?: return
            binding.weekCalendar.scrollToWeek(targetDate)
        } else {
            val targetMonth = binding.weekCalendar.findLastVisibleDay()?.date?.yearMonth ?: return
            binding.monthCalendar.scrollToMonth(targetMonth)
        }

        calendarToggleAnimation(calendarType)
    }

    private fun calendarToggleAnimation(calendarType: CalendarType) {
        val weekHeight = binding.weekCalendar.height
        val visibleMonthHeight = weekHeight *
                binding.monthCalendar.findFirstVisibleMonth()?.weekDays.orEmpty().count()

        val oldHeight = if (calendarType == CalendarType.WEEK) visibleMonthHeight else weekHeight
        val newHeight = if (calendarType == CalendarType.WEEK) weekHeight else visibleMonthHeight

        val animator = ValueAnimator.ofInt(oldHeight, newHeight)
        animator.addUpdateListener { anim ->
            binding.monthCalendar.updateLayoutParams {
                height = anim.animatedValue as Int
            }
            // A bug is causing the month calendar to not redraw its children
            // with the updated height during animation, this is a workaround.
            binding.monthCalendar.children.forEach { child ->
                child.requestLayout()
            }
        }

        animator.doOnStart {
            if (calendarType != CalendarType.WEEK) {
                binding.weekCalendar.isInvisible = true
                binding.monthCalendar.isVisible = true
            } else {
                binding.monthCalendar.updateLayoutParams {
                    height =
                        ViewGroup.LayoutParams.WRAP_CONTENT
                }
                updateTitle(calendarType)
            }
            animator.duration = 250
            animator.start()
        }
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