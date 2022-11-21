package com.example.to_dolistclone.feature.common.dialog

import androidx.fragment.app.FragmentManager
import com.example.to_dolistclone.feature.common.dialog.modal_bottom_sheet.TodoShortcut
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class DialogsManager @Inject constructor(private val fragmentManager: FragmentManager) {

    fun createTaskModalBottomSheet(date: LocalDate? = null) {
        val todoShortcut = TodoShortcut(date)
        todoShortcut.show(fragmentManager, TodoShortcut.TAG)
    }

    fun createAddCategoryDialogFragment(saveClickListener: (String) -> Unit) {
        val addCategoryDialogFragment = AddCategoryDialogFragment(saveClickListener)
        addCategoryDialogFragment.show(fragmentManager, AddCategoryDialogFragment.TAG)
    }

    fun createDateTimePickerDialog() {
        val dateTimePickerDialogFragment = DateTimePickerDialogFragment()
        dateTimePickerDialogFragment.show(fragmentManager, DateTimePickerDialogFragment.TAG)
    }

    fun createReminderDialog() {
        val reminderDialogFragment = ReminderDialogFragment()
        reminderDialogFragment.show(fragmentManager, ReminderDialogFragment.TAG)
    }

    fun createReminderDateTimePickerDialog(onClick: (LocalDateTime) -> Unit) {
        val reminderDateTimePickerDialog = ReminderDateTimePickerDialogFragment { onClick(it) }
        reminderDateTimePickerDialog.show(fragmentManager, ReminderDateTimePickerDialogFragment.TAG)
    }

}