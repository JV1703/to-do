package com.example.to_dolistclone.feature.common

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.databinding.FragmentTimePickerBinding
import com.example.to_dolistclone.feature.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TimePickerFragment(private val onClick: (LocalTime) -> Unit) :
    BaseFragment<FragmentTimePickerBinding>(FragmentTimePickerBinding::inflate) {

    @Inject
    lateinit var dateUtil: DateUtil

    private val myCalendar: Calendar = Calendar.getInstance()

    private val timePickerOnDataSetListener =
        TimePicker.OnTimeChangedListener { _, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            val date = myCalendar.time
            val localTime = dateUtil.toLocalTime(date)
            onClick(localTime)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.timePicker.setOnTimeChangedListener(timePickerOnDataSetListener)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.timePicker.hour = myCalendar.get(Calendar.HOUR_OF_DAY)
            binding.timePicker.minute = myCalendar.get(Calendar.MINUTE)
        } else {
            binding.timePicker.currentHour = myCalendar.get(Calendar.HOUR_OF_DAY)
            binding.timePicker.currentMinute = myCalendar.get(Calendar.MINUTE)
        }
    }
}