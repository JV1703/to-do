package com.example.to_dolistclone.core.utils.ui

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

fun Activity.makeToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.makeToast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun <T> AppCompatActivity.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}

fun <T> Fragment.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}

fun <T> AppCompatActivity.collectLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(collect)
        }
    }
}

fun <T> Fragment.collectLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(collect)
        }
    }
}

fun ImageView.pulseAnimation(
    scaleX: Float = 1.5F,
    ScaleY: Float = 1.5F,
    alpha: Float = 0F,
    duration: Long = 1000,
    repeatCount: Int = ObjectAnimator.INFINITE,
    repeatMode: Int = ObjectAnimator.RESTART
) {
    val pulse = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("scaleX", scaleX),
        PropertyValuesHolder.ofFloat("scaleY", ScaleY),
        PropertyValuesHolder.ofFloat("alpha", alpha)
    )
    pulse.duration = duration
    pulse.repeatCount = repeatCount
    pulse.repeatMode = repeatMode
    pulse.start()
}

fun Chip.transformIntoDatePicker(
    context: Context, maxDate: Date? = null, callback: (LocalDate) -> Unit
) {
    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val myCalendar = Calendar.getInstance()
    val datePickerOnDateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val date = myCalendar.time
            val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            callback(localDate)
        }

    setOnClickListener {
        DatePickerDialog(
            context,
            datePickerOnDateSetListener,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).run {
            maxDate?.time?.also { datePicker.maxDate = it }
            show()
        }
    }
}

fun TextView.transformIntoDatePicker(
    context: Context, maxDate: Date? = null, callback: (LocalDate) -> Unit
) {
    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val myCalendar = Calendar.getInstance()
    val datePickerOnDateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val date = myCalendar.time
            val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            callback(localDate)
        }

    setOnClickListener {
        DatePickerDialog(
            context,
            datePickerOnDateSetListener,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).run {
            maxDate?.time?.also { datePicker.maxDate = it }
            show()
        }
    }
}

fun TextInputLayout.transformIntoDatePicker(
    context: Context, maxDate: Date? = null, callback: (LocalDate) -> Unit
) {
    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val myCalendar = Calendar.getInstance()
    val datePickerOnDateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val date = myCalendar.time
            val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            callback(localDate)
        }

    setOnClickListener {
        DatePickerDialog(
            context,
            datePickerOnDateSetListener,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).run {
            maxDate?.time?.also { datePicker.maxDate = it }
            show()
        }
    }
}

fun TextInputEditText.transformIntoDatePicker(
    context: Context, maxDate: Date? = null, callback: (LocalDate) -> Unit
) {
    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val myCalendar = Calendar.getInstance()
    val datePickerOnDateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val date = myCalendar.time
            val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            callback(localDate)
        }

    setOnClickListener {
        DatePickerDialog(
            context,
            datePickerOnDateSetListener,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).run {
            maxDate?.time?.also { datePicker.maxDate = it }
            show()
        }
    }
}

fun TextView.transformIntoTimePicker(
    context: Context, is24HourView: Boolean, callback: (LocalTime) -> Unit
) {
    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val myCalendar = Calendar.getInstance()
    val timePickerOnDataSetListener =
        TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            val date = myCalendar.time
            val localTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
            callback(localTime)
        }

    setOnClickListener {
        TimePickerDialog(
            context,
            timePickerOnDataSetListener,
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            is24HourView
        ).show()
    }
}

//fun EditText.onKeyboardEnter(clearText: Boolean, callback: (String) -> Unit){
//    this.setOnKeyListener {
//            view, keyCode, keyEvent ->
//        if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
//            if ((view as AppCompatEditText).text.toString().trim().isNotEmpty()) {
//                callback(view.text.toString().trim())
//            }
//
//            if(clearText){
//                this.text.clear()
//            }
//            true
//        } else false
//    }
//}

fun AppCompatEditText.onKeyboardEnter(clearText: Boolean, callback: (String) -> Unit) {
    this.setOnEditorActionListener { textView, actionId, keyEvent ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if ((textView as AppCompatEditText).text.toString().trim().isNotEmpty()) {
                callback(textView.text.toString().trim())
            }

            if (clearText) {
                this.text?.clear()
            }

            true
        } else false
    }

}