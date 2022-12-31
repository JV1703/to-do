package com.example.to_dolistclone.feature.common.dialog

import androidx.fragment.app.FragmentManager
import com.example.to_dolistclone.feature.todo_shortcut.TodoShortcut
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class DialogsManager @Inject constructor(private val fragmentManager: FragmentManager) {

    fun showTodoShortcut(date: LocalDate? = null) {
        val todoShortcut = TodoShortcut(date)
        todoShortcut.show(fragmentManager, TodoShortcut.TAG)
    }

    fun showAddCategoryDialogFragment(saveClickListener: (String) -> Unit) {
        val addCategoryDialogFragment = AddCategoryDialogFragment(saveClickListener)
        addCategoryDialogFragment.show(fragmentManager, AddCategoryDialogFragment.TAG)
    }

    fun showReminderDateTimePickerDialog(onClick: (LocalDateTime) -> Unit) {
        val reminderDateTimePickerDialog = ReminderDateTimePickerDialogFragment { onClick(it) }
        reminderDateTimePickerDialog.show(fragmentManager, ReminderDateTimePickerDialogFragment.TAG)
    }

}