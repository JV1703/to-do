package com.example.to_dolistclone.feature.common.dialog.modal_bottom_sheet

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.utils.ui.collectLatestLifecycleFlow
import com.example.to_dolistclone.core.utils.ui.makeToast
import com.example.to_dolistclone.core.utils.ui.transformIntoDatePicker
import com.example.to_dolistclone.databinding.ModalBottomSheetContentBinding
import com.example.to_dolistclone.feature.common.CategoryPopupMenu
import com.example.to_dolistclone.feature.common.dialog.DialogsManager
import com.example.to_dolistclone.feature.home.adapter.TaskAdapter
import com.example.to_dolistclone.feature.home.adapter.TaskProxy
import com.example.to_dolistclone.feature.todo.TodoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TodoShortcut(date: LocalDate?/*, private val todoShortcutListener: TodoShortcutListener*/) : BottomSheetDialogFragment() {

    @Inject
    lateinit var dialogsManager: DialogsManager

    @Inject
    lateinit var dateUtil: DateUtil

    @Inject
    lateinit var categoryPopupMenu: CategoryPopupMenu

    private val viewModel: TodoViewModel by activityViewModels()

    private var _binding: ModalBottomSheetContentBinding? = null
    private val binding get() = _binding!!
    private lateinit var tasksAdapter: TaskAdapter

    private val zoneId = ZoneId.systemDefault()
    private var dueDate: Long? = date?.atStartOfDay()?.atZone(zoneId)?.toEpochSecond()
    private var reminderDate: Long? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(null)
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

        setupTasksAdapter()

        dueDate?.let {
            binding.deadline.text = getDateString(dateUtil.toLocalDate(it))
            binding.deadline.isCloseIconVisible = true
        }

        binding.addSubTask.setOnClickListener {
            tasksAdapter.addTask()
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
            dialogsManager.createReminderDateTimePickerDialog {
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

        collectLatestLifecycleFlow(viewModel.taskUiState) { uiState ->
            binding.category.setOnClickListener {
                showCategoryPopupMenu(it, uiState.categories, uiState.selectedCategory)
            }
            binding.category.text = uiState.selectedCategory ?: "No Category"
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ModalBottomSheet"
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

    private fun showCategoryPopupMenu(
        view: View, categories: Set<String>, selectedCategory: String?
    ) {
        categoryPopupMenu.build(
            view, categories, selectedCategory
        ) { menuItem ->
            when (menuItem.title) {
                "Create New" -> {
                    dialogsManager.createAddCategoryDialogFragment{ categoryName ->
                        if (categoryName.isNotEmpty()) {
                            viewModel.insertTodoCategory(categoryName)
                        }else{
                            makeToast("Please provide name for category")
                        }
                    }
                    true
                }
                else -> {
                    makeToast(menuItem.title.toString())
                    viewModel.updateSelectedCategory(menuItem.title.toString())
                    binding.category.text = menuItem.title
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
                todoCategoryRefName = binding.category.text.toString())

            viewModel.insertTodo(tasksProxy, todo)
            dismiss()
        }
    }
}