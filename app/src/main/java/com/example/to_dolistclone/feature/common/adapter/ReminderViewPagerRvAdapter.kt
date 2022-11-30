package com.example.to_dolistclone.feature.common.adapter

import android.content.res.Resources.NotFoundException
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistclone.databinding.FragmentDatePickerBinding
import com.example.to_dolistclone.databinding.FragmentTimePickerBinding
import java.text.SimpleDateFormat
import java.util.*

class ReminderViewPagerRvAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class DatePickerViewHolder(private val binding: FragmentDatePickerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val myCalendar: Calendar = Calendar.getInstance()
        val datePickerOnDateSetListener =
            DatePicker.OnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                Log.i("date_picker", sdf.format(myCalendar.time))
            }

        fun setupDatePicker() {
            binding.datePicker.init(
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH),
                datePickerOnDateSetListener
            )
        }
    }

    class TimePickerViewHolder(private val binding: FragmentTimePickerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val myCalendar: Calendar = Calendar.getInstance()
        private val timePickerOnDataSetListener =
            TimePicker.OnTimeChangedListener { _, hourOfDay, minute ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar.set(Calendar.MINUTE, minute)
                val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                Log.i("time_picker", sdf.format(myCalendar.time))
            }

        fun setupTimePicker() {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> {
                DatePickerViewHolder(
                    FragmentDatePickerBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
            }
            1 -> {
                TimePickerViewHolder(
                    FragmentTimePickerBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
            }
            else -> throw NotFoundException("shit happens")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DatePickerViewHolder -> {
                holder.setupDatePicker()
            }
            is TimePickerViewHolder -> {
                holder.setupTimePicker()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return 2
    }
}