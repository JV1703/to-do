package com.example.to_dolistclone.feature.common

import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.databinding.FragmentDatePickerBinding
import com.example.to_dolistclone.feature.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DatePickerFragment(private val onClick: (LocalDate) -> Unit) :
    BaseFragment<FragmentDatePickerBinding>(FragmentDatePickerBinding::inflate) {

    @Inject
    lateinit var dateUtil: DateUtil

    private val myCalendar: Calendar = Calendar.getInstance()

    private val datePickerOnDateSetListener =
        DatePicker.OnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val date = myCalendar.time
            val localDate = dateUtil.toLocalDate(date)
            onClick(localDate)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.datePicker.init(
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH),
            datePickerOnDateSetListener
        )
    }

}