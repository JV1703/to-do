package com.example.to_dolistclone.feature.todo_shortcut

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.common.*
import com.example.to_dolistclone.core.utils.ui.collectLatestLifecycleFlow
import com.example.to_dolistclone.core.utils.ui.makeToast
import com.example.to_dolistclone.core.utils.ui.setStringSpanColor
import com.example.to_dolistclone.core.utils.ui.transformIntoDatePicker
import com.example.to_dolistclone.databinding.ModalBottomSheetContentBinding
import com.example.to_dolistclone.feature.common.dialog.DialogsManager
import com.example.to_dolistclone.feature.common.popup_menu.CategoryPopupMenu
import com.example.to_dolistclone.feature.home.adapter.TaskAdapter
import com.example.to_dolistclone.feature.home.adapter.TaskProxy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TodoShortcut(private val date: LocalDate? = null) : BottomSheetDialogFragment() {

    @Inject
    lateinit var dialogsManager: DialogsManager

    @Inject
    lateinit var dateUtil: DateUtil

    @Inject
    lateinit var categoryPopupMenu: CategoryPopupMenu

    private val viewModel: TodoShortcutViewModel by viewModels()

    private var _binding: ModalBottomSheetContentBinding? = null
    private val binding get() = _binding!!


    private lateinit var tasksAdapter: TaskAdapter
    private lateinit var alarmManager: AlarmManager

    private var dueDate: Long? = null
    private var reminderDate: Long? = null

    private val todoId = UUID.randomUUID().toString()
    private val alarmRef = Random().nextInt()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ModalBottomSheetContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let{ sheet ->
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                sheet.parent.parent.requestLayout()
            }
        }

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        setupTasksAdapter()

        dueDate = date?.let {
            dateUtil.toLong(it)
        }

        dueDate?.let {
            binding.deadline.text = getDateString(dateUtil.toLocalDate(it))
            binding.deadline.isCloseIconVisible = true
        }

        binding.addSubTask.setOnClickListener {
            val taskSize = tasksAdapter.getTasks().size
            tasksAdapter.addTask()
            binding.taskRv.scrollToPosition(taskSize - 1)
        }

        binding.insertTodoBtn.setOnClickListener {
            insertTodo(tasksAdapter.getTasks())
        }

        binding.deadline.transformIntoDatePicker(requireContext()) {
            val dateString = getDateString(it)
            dueDate = dateUtil.toLong(it)
            binding.apply {
                deadline.isCloseIconVisible = true
                deadline.text = dateString
            }
        }

        binding.reminder.setOnClickListener {
            dialogsManager.showReminderDateTimePickerDialog {
                val dateString = getDateString(it)
                val timeString = dateUtil.toString(it, "hh:mm a", Locale.getDefault())
                reminderDate = dateUtil.toLong(it)
                binding.apply {
                    reminder.isCloseIconVisible = true
                    reminder.text = "$dateString ($timeString)"
                }
            }
        }

        binding.chipGroup.children.forEach {
            val chip = it as Chip
            chip.setOnCloseIconClickListener {
                chip.isCloseIconVisible = false
                chip.text = null

                if (chip.id == binding.deadline.id) {
                    dueDate = null
                }
                if (chip.id == binding.reminder.id) {
                    reminderDate = null
                }
            }
        }

        binding.category.setOnClickListener {
            categoryPopupMenu.showCategoryPopupMenu()
        }

        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->
            setupCategoryPopupMenu(binding.category, uiState.categories, uiState.selectedCategory)

            uiState.errorMsg?.let {
                makeToast(it)
                viewModel.errorMessageShown()
            }

            binding.category.text = uiState.selectedCategory ?: "No Category"
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDateString(localDate: LocalDate): String {
        return if (localDate.year == LocalDate.now().year) {
            dateUtil.toString(localDate, "EEE, MMM dd", Locale.getDefault())
        } else {
            dateUtil.toString(localDate, "EEE, MMM dd, yyyy", Locale.getDefault())
        }
    }

    private fun getDateString(localDateTime: LocalDateTime): String {
        return if (localDateTime.year == LocalDateTime.now().atZone(dateUtil.zoneId).year) {
            dateUtil.toString(localDateTime, "EEE, MMM dd", Locale.getDefault())
        } else {
            dateUtil.toString(localDateTime, "EEE, MMM dd, yyyy", Locale.getDefault())
        }
    }

    private fun setupCategoryPopupMenu(
        view: View, categories: Set<String>, selectedCategory: String?
    ) {
        categoryPopupMenu.build(
            view, categories, selectedCategory
        ) { menuItem ->
            when (menuItem.title) {
                "Create New" -> {
                    dialogsManager.showAddCategoryDialogFragment { categoryName ->
                        if (categoryName.isNotEmpty()) {
                            viewModel.insertTodoCategory(
                                categoryName = categoryName
                            )
                            viewModel.updateSelectedCategory(categoryName)
                        } else {
                            makeToast("Please provide name for category")
                        }
                    }
                    true
                }
                else -> {
                    viewModel.updateSelectedCategory(menuItem.title.toString())
                    binding.category.text = setStringSpanColor(
                        requireContext(),
                        menuItem.title.toString(),
                        android.R.color.tab_indicator_text
                    )
                    true
                }
            }
        }
    }

    private fun setupTasksAdapter() {
        tasksAdapter = TaskAdapter()
        binding.taskRv.adapter = tasksAdapter
    }

    private fun insertTodo(tasksProxy: List<TaskProxy>) {
        val isTaskEmpty = tasksProxy.isEmpty()
        if (binding.titleTv.text.toString().isEmpty()) {
            makeToast("Please insert title before saving")
        } else {
            val todo = viewModel.createTodo(
                todoId = todoId,
                title = binding.titleTv.text.toString().trim(),
                deadline = dueDate,
                reminder = reminderDate,
                repeat = null,
                isComplete = false,
                createdOn = dateUtil.getCurrentDateTimeLong(),
                completedOn = null,
                tasks = !isTaskEmpty,
                notes = false,
                attachments = false,
                alarmRef = if (reminderDate != null) alarmRef else null,
                todoCategoryRefName = binding.category.text.toString()
            )

            viewModel.insertTodo(tasksProxy, todo)

            if (todo.reminder == null) {
                todo.alarmRef?.let {
                    cancelAlarm(it)
                }
            } else {
                setAlarm(todo.reminder, todo.title)
            }

            dismiss()
        }
    }

    private fun setAlarm(reminderAt: Long, title: String) {
        val flags =
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra(TODO_ID, todoId)
        intent.putExtra(ALARM_REF, alarmRef)
        intent.putExtra(NOTIFICATION_TITLE, title)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), alarmRef, intent, flags)
        alarmManager.setExact(AlarmManager.RTC, reminderAt, pendingIntent)

        val noCreateFlag =
            PendingIntent.FLAG_NO_CREATE or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0

        val testAlarm = (PendingIntent.getBroadcast(
            requireContext(),
            alarmRef,
            Intent(requireContext(), AlarmReceiver::class.java),
            noCreateFlag
        ) != null)

        if (testAlarm) {
            Log.i("alarmManager", "alarm already exist")
        } else {
            Log.i("alarmManager", "new alarm")
        }
    }

    private fun cancelAlarm(alarmRef: Int) {
        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), alarmRef, intent, flags)
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}