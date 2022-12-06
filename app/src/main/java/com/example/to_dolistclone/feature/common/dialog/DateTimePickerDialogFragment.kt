package com.example.to_dolistclone.feature.common.dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.utils.ui.makeToast
import com.example.to_dolistclone.databinding.DateTimePickerDialogFragmentBinding
import com.example.to_dolistclone.feature.calendar.displayText
import com.example.to_dolistclone.feature.calendar.misc.MonthDayViewContainer
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DateTimePickerDialogFragment : DialogFragment() {

    private var _binding: DateTimePickerDialogFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var dialogsManager: DialogsManager

    @RequiresApi(Build.VERSION_CODES.O)
    private val today = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedDate: LocalDate? = today

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DateTimePickerDialogFragmentBinding.inflate(layoutInflater)

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        val daysOfWeek = daysOfWeek()

        setupCalendar(startMonth, endMonth, currentMonth, daysOfWeek)

        binding.monthCalendar.monthScrollListener = { updateTitle() }

        binding.apply {
            nextMonth.setOnClickListener { scrollToNextMonth() }
            prevMonth.setOnClickListener { scrollToPrevMonth() }
        }
        binding.noDateSwitch.setOnCheckedChangeListener { _, b ->

            binding.modContainer.children.forEach {
                it.isEnabled = !b
            }

            if (b) {
                selectDate(today)
                selectedDate = null

//                binding.timeValueTv.text = "No"
                binding.repeatValueTv.text = "No Repeat"
            }
        }

        binding.cancelTv.setOnClickListener { dismiss() }
        binding.doneTv.setOnClickListener {
            val localDate = selectedDate
            val formatter = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy", Locale.getDefault())
            localDate?.let {
                makeToast(it.format(formatter))
            }
            dismiss()
        }

        binding.reminderTv.setOnClickListener {
            dialogsManager.showReminderDateTimePickerDialog {
                val dateFormatter =
                    DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy", Locale.getDefault())
                val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
                val dateString = it.format(dateFormatter)
                val timeString = it.format(timeFormatter)
                binding.reminderTv.text = "Remind me at $timeString\n$dateString"
            }
        }

        return AlertDialog.Builder(requireContext()).setView(binding.root).create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {
        binding.legendLayout.root.children.map { it as TextView }
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

        @RequiresApi(Build.VERSION_CODES.O)
        override fun create(view: View): MonthDayViewContainer = MonthDayViewContainer(view) {
            monthCalendarOnClickListener(it)
            makeToast(it.toString())
            Log.i("calendar_tag", "date: ${it.date}, position: ${it.position}")
            Log.i(
                "calendar_tag",
                "dayOfWeek: ${today.dayOfWeek}, month: ${today.month}, year: ${today.year}, month value: ${today.monthValue}, day of month: ${today.dayOfMonth}, day of year${today.dayOfYear}, year month: ${today.yearMonth}"
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindDate(date: LocalDate, textView: TextView, eventInd: View) {
        textView.text = date.dayOfMonth.toString()
        when (date) {
            today -> {
                textView.setBackgroundResource(R.drawable.calendar_today_bg)
                eventInd.visibility = View.INVISIBLE
            }
            selectedDate -> {
                textView.setBackgroundResource(R.drawable.calendar_selected_bg)
                eventInd.visibility = View.INVISIBLE
            }
            else -> {
                textView.background = null
                eventInd.isVisible = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.monthCalendar.notifyDateChanged(it) }
            binding.monthCalendar.notifyDateChanged(date)
            Log.i("DateTimePickerDialog", "selectedDate: $selectedDate")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    companion object {
        const val TAG = "Date Time Picker Dialog Fragment"
    }

}