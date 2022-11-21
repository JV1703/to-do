package com.example.to_dolistclone.feature.common.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.utils.ui.makeToast
import com.example.to_dolistclone.databinding.ReminderDialogFragmentBinding
import com.example.to_dolistclone.feature.common.adapter.ReminderAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderDialogFragment : DialogFragment() {

    private var _binding: ReminderDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var reminderAdapter: ReminderAdapter

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(null)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ReminderDialogFragmentBinding.inflate(layoutInflater)
        dialog?.setCancelable(false)

        setupReminder()
        setupReminderType()

        binding.reminderSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (!b) {
                binding.containerBody.children.forEach {
                    it.isEnabled = false
                }
            }
        }

        binding.cancelTv.setOnClickListener {
            dismiss()
        }

        binding.doneTv.setOnClickListener {
            dismiss()
        }

        binding.doneTv.setOnClickListener {
            makeToast(binding.reminderActv.text.toString())
        }
        return AlertDialog.Builder(requireContext()).setView(binding.root).create()
    }

    private fun setupReminder() {
        val reminders = resources.getStringArray(R.array.reminder_array)
        val reminderAdapter =
            ArrayAdapter(requireContext(), R.layout.reminder_array_item, reminders)
        binding.reminderActv.setAdapter(reminderAdapter)
    }

    private fun setupReminderType() {
        val arrays = listOf(
            "Same with due date",
            "5 minutes before",
            "10 minutes before",
            "15 minutes before",
            "30 minutes before",
            "1 day before"
        )
        val reminders = resources.getStringArray(R.array.reminder_type_array)
        val reminderAdapter =
            ArrayAdapter(requireContext(), R.layout.reminder_array_item, reminders)
        binding.reminderTypeActv.setAdapter(reminderAdapter)
    }

    companion object {
        const val TAG = "Reminder Picker Dialog Fragment"
    }

}