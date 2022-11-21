package com.example.to_dolistclone.feature.detail.ui.details

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.common.*
import com.example.to_dolistclone.core.domain.model.Task
import com.example.to_dolistclone.core.utils.ui.*
import com.example.to_dolistclone.databinding.FragmentDetailsBinding
import com.example.to_dolistclone.feature.BaseFragment
import com.example.to_dolistclone.feature.common.CategoryPopupMenu
import com.example.to_dolistclone.feature.common.dialog.DialogsManager
import com.example.to_dolistclone.feature.detail.adapter.*
import com.example.to_dolistclone.feature.detail.viewmodel.DetailsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DetailsFragment : BaseFragment<FragmentDetailsBinding>(FragmentDetailsBinding::inflate),
    StartDragListener, DragAndDropController.ItemTouchHelperContract, DetailTaskAdapterListener {

    @Inject
    lateinit var dialogsManager: DialogsManager

    @Inject
    lateinit var dateUtil: DateUtil

    @Inject
    lateinit var categoryPopupMenu: CategoryPopupMenu



    private val viewModel: DetailsViewModel by activityViewModels()
    private lateinit var taskAdapter: DetailTaskAdapter
    private lateinit var dragHelper: ItemTouchHelper
    private lateinit var alarmManager: AlarmManager

    private var todoId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupTaskRv()

        binding.titleTv.onKeyboardEnter(false) {
            viewModel.updateTodoTitle(title = it)
            if (it.isEmpty()) {
                makeToast("Please set title.")
            }
        }

        binding.taskEt.onKeyboardEnter(true) {
            viewModel.insertTask(title = it, position = taskAdapter.currentList.size)
            if (taskAdapter.currentList.size == 0) {
                viewModel.updateTodoTasksAvailability(true)
            }
        }

        binding.checkbox.setOnCheckedChangeListener { _, isComplete ->
            viewModel.updateTodoCompletion(
                isComplete
            )
        }

        binding.dueDateTv.transformIntoDatePicker(requireContext()) {
            binding.dueDateTv.text = getDateString(dateUtil.toLocalDateTime(it))
            viewModel.updateDeadline(dateUtil.toLong(it))
            binding.dueDateTvClear.isGone = false
        }

        onClearText(binding.dueDateTvClear, binding.dueDateTv, "Due Date") {
            viewModel.updateDeadline(null)
        }

        onClearText(binding.reminderTvClear, binding.reminderTv, "Reminder") {
            viewModel.updateReminder(null)
            cancelAlarm()
        }

        binding.notesTv.setOnClickListener {
            val action = DetailsFragmentDirections.actionDetailsFragmentToNoteFragment()
            findNavController().navigate(action)
        }

        onClearText(binding.notesTvClear, binding.notesTv, "Repeat") {
            makeToast("Reminder tapped")
            viewModel.deleteNote()
        }

        binding.deleteIv.setOnClickListener {
            requireActivity().finish()
            viewModel.deleteTodo()
        }

        collectLifecycleFlow(viewModel.todoId) {
            todoId = it
        }

        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->

            taskAdapter.submitList(uiState.todoDetails?.tasks)

            binding.titleTv.setText(uiState.todoDetails?.todo?.title)

            binding.categoryTv.text = uiState.todoDetails?.todo?.todoCategoryRefName

            binding.dueDateTvClear.isGone = uiState.todoDetails?.todo?.deadline == null
            binding.reminderTvClear.isGone = uiState.todoDetails?.todo?.reminder == null
            binding.repeatTvClear.isGone = uiState.todoDetails?.todo?.repeat.isNullOrEmpty()
            binding.notesTvClear.isGone = uiState.todoDetails?.note == null
            binding.attachmentTvClear.isGone = uiState.todoDetails?.attachments.isNullOrEmpty()
            Log.i("attachment", "${uiState.todoDetails?.attachments == null} ")

            uiState.todoDetails?.todo?.isComplete?.let {
                binding.checkbox.isChecked = it
            }

            uiState.todoDetails?.todo?.deadline?.let { deadline ->
                binding.dueDateTv.text = getDateString(dateUtil.toLocalDateTime(deadline))
            }

            uiState.todoDetails?.todo?.reminder?.let { reminder ->
                binding.reminderTv.text = generateReminderString(reminder)
            }

            if (uiState.todoDetails?.note == null) {
                binding.notesTv.text = "Notes"
            } else {
                binding.notesTv.text = uiState.todoDetails.note.title
            }

            binding.categoryContainer.setOnClickListener {
                showCategoryPopupMenu(
                    it, uiState.categories, uiState.todoDetails?.todo?.todoCategoryRefName
                )
            }

            uiState.todoDetails?.todo?.createdOn?.let {
                binding.createdOnTv.text = "Created on ${getDateString(dateUtil.toLocalDateTime(it))}"
            }

            binding.reminderTv.setOnClickListener {
                dialogsManager.createReminderDateTimePickerDialog { reminder ->
                    binding.reminderTv.text = generateReminderString(dateUtil.toLong(reminder))
                    viewModel.updateReminder(dateUtil.toLong(reminder))
//                    setAlarm(dateUtil.toLong(reminder))
                    binding.dueDateTvClear.isGone = false
                }
            }

        }

    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }

    private fun setupTaskRv() {
        taskAdapter = DetailTaskAdapter(this, this)
        enableDragAndDrop()
        enableSwipeToDeleteAndUndo()
        binding.taskRv.adapter = taskAdapter
    }

    private fun generateReminderString(reminder: Long): String {
        val date = getDateString(dateUtil.toLocalDateTime(reminder))
        val time = dateUtil.toString(reminder, "hh:mm a", Locale.getDefault())
        return "Remind me at $time\n$date"
    }

    private fun getDateString(localDateTime: LocalDateTime): String {
        return if (localDateTime.year == LocalDateTime.now().atZone(dateUtil.zoneId).year) {
            dateUtil.toString(localDateTime, "EEE, MMM dd", Locale.getDefault())
        } else {
            dateUtil.toString(localDateTime, "EEE, MMM dd, yyyy", Locale.getDefault())
        }
    }

    override fun requestDrag(viewHolder: RecyclerView.ViewHolder) {
        dragHelper.startDrag(viewHolder)
    }

    private fun enableDragAndDrop() {
        val callback: ItemTouchHelper.Callback = DragAndDropController(this)
        dragHelper = ItemTouchHelper(callback)
        dragHelper.attachToRecyclerView(binding.taskRv)
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback = object : SwipeController(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val task = taskAdapter.currentList[position]
                if (taskAdapter.currentList.size == 1) {
                    viewModel.updateTodoTasksAvailability(false)
                }
                viewModel.deleteTask(task.taskId)
                undoDeleteTask(task)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.taskRv)
    }

    private fun undoDeleteTask(task: Task) {
        Snackbar.make(binding.root, "Todo is deleted", Snackbar.LENGTH_SHORT).apply {
            setAction("Undo") {
                viewModel.restoreDeletedTaskProxy(task)
                viewModel.updateTodoTasksAvailability(true)
            }
            setActionTextColor(Color.YELLOW)
            show()
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
                    dialogsManager.createAddCategoryDialogFragment { categoryName ->
                        if (categoryName.isNotEmpty()) {
                            viewModel.insertTodoCategory(categoryName)
                            viewModel.updateTodoCategory(categoryName)
                        } else {
                            makeToast("Please provide name for category")
                        }
                    }
                    true
                }
                else -> {
                    makeToast(menuItem.title.toString())
                    viewModel.updateTodoCategory(menuItem.title.toString())
                    binding.categoryTv.text = menuItem.title
                    true
                }
            }
        }
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        val list = taskAdapter.currentList.toMutableList()
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(list, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(list, i, i - 1)
            }
        }
        taskAdapter.submitList(list)
        viewModel.updateTaskPosition(list)
    }

    override fun onRowSelected(myViewHolder: DetailTaskAdapter.DetailTaskViewHolder?) {
        myViewHolder?.itemView?.alpha = 0.5F
    }

    override fun onRowClear(myViewHolder: DetailTaskAdapter.DetailTaskViewHolder?) {
        myViewHolder?.itemView?.alpha = 1.0f
    }

    override fun updateDb() {
        val list = taskAdapter.currentList
        list.forEachIndexed { index, task ->
            viewModel.updateTaskPosition(task.taskId, index)
        }
    }

    override fun updateTaskTitle(taskId: String?, title: String) {
        Log.i("taskId", "$taskId")
        viewModel.updateTaskTitle(taskId!!, title)
    }

    override fun deleteTask(taskId: String) {
        viewModel.deleteTask(taskId)
    }

    override fun updateTaskCompletion(taskId: String?, isComplete: Boolean) {
        viewModel.updateTaskCompletion(taskId!!, isComplete)
    }

    private fun onClearText(
        imageView: AppCompatImageView,
        textView: AppCompatTextView,
        text: String,
        callback: () -> Unit
    ) {
        imageView.setOnClickListener {
            textView.text = text
            callback()
            imageView.isGone = true
        }
    }

    private fun setAlarm(reminderAt: Long, title: String, body: String){
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra(TODO_ID, todoId)
        intent.putExtra(NOTIFICATION_TITLE, title)
        intent.putExtra(NOTIFICATION_BODY, body)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
        alarmManager.set(AlarmManager.RTC, reminderAt, pendingIntent)
    }

    private fun cancelAlarm(){
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
        alarmManager.cancel(pendingIntent)
    }

}