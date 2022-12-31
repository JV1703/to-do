package com.example.to_dolistclone.feature.common.dialog

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Dialog
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.databinding.ReminderDateTimePickerDialogFragmentBinding
import com.example.to_dolistclone.feature.common.DatePickerFragment
import com.example.to_dolistclone.feature.common.TimePickerFragment
import com.example.to_dolistclone.feature.common.adapter.ReminderViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ReminderDateTimePickerDialogFragment(private val onClick: (LocalDateTime) -> Unit) :
    AppCompatDialogFragment() {

    @Inject
    lateinit var dateUtil: DateUtil

    private var _binding: ReminderDateTimePickerDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private var date: LocalDate? = null
    private var time: LocalTime? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = ReminderDateTimePickerDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabLayout()

        if (!checkNotificationPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }

        binding.cancelTv.setOnClickListener {
            dismiss()
        }
        binding.doneTv.setOnClickListener {
            getLocalDateTime()
            dismiss()
        }
    }

    private fun setupTabLayout() {
        val fragmentLists = listOf(DatePickerFragment {
            date = it
            binding.viewPager.setCurrentItem(1, true)
        }, TimePickerFragment {
            time = it
        })
        binding.viewPager.adapter = ReminderViewPagerAdapter(
            fragmentLists, childFragmentManager, viewLifecycleOwner.lifecycle
        )
        binding.viewPager.isUserInputEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, index ->

            when (index) {
                0 -> {
                    tab.text = "DATE"
                }
                1 -> {
                    tab.text = "TIME"
                }
                else -> {
                    throw NotFoundException("Position not found")
                }
            }
        }.attach()
    }

    private fun getCurrentDate() {
        date = dateUtil.toLocalDate(Calendar.getInstance().time)
    }

    private fun setDefaultTime() {
        time = dateUtil.toLocalTime("18:00")
    }

    private fun getLocalDateTime() {
        if (date != null && time != null) {
            onClick(LocalDateTime.of(date, time))
        }

        if (date != null && time == null) {
            setDefaultTime()
            onClick(LocalDateTime.of(date, time))
        }

        if (date == null) {
            getCurrentDate()
            setDefaultTime()
            onClick(LocalDateTime.of(date, time))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "Reminder Date Time Picker Dialog Fragment"
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireContext(),
                POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}