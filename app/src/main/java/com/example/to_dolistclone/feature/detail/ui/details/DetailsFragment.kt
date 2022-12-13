package com.example.to_dolistclone.feature.detail.ui.details

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.common.*
import com.example.to_dolistclone.core.domain.model.Attachment
import com.example.to_dolistclone.core.domain.model.Task
import com.example.to_dolistclone.core.utils.ui.*
import com.example.to_dolistclone.databinding.FragmentDetailsBinding
import com.example.to_dolistclone.feature.BaseFragment
import com.example.to_dolistclone.feature.common.dialog.DialogsManager
import com.example.to_dolistclone.feature.common.popup_menu.CategoryPopupMenu
import com.example.to_dolistclone.feature.detail.adapter.*
import com.example.to_dolistclone.feature.detail.adapter.attachment.DetailAttachmentAdapter
import com.example.to_dolistclone.feature.detail.adapter.attachment.DetailAttachmentAdapterListener
import com.example.to_dolistclone.feature.detail.adapter.task.*
import com.example.to_dolistclone.feature.detail.viewmodel.DetailsViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import kotlin.random.Random.Default.nextInt

@AndroidEntryPoint
class DetailsFragment : BaseFragment<FragmentDetailsBinding>(FragmentDetailsBinding::inflate),
    StartDragListener, DragAndDropController.ItemTouchHelperContract, DetailTaskAdapterListener,
    DetailAttachmentAdapterListener {

    @Inject
    lateinit var dialogsManager: DialogsManager

    @Inject
    lateinit var dateUtil: DateUtil

    @Inject
    lateinit var categoryPopupMenu: CategoryPopupMenu

    @Inject
    lateinit var notification: ReminderNotificationService

    @Inject
    lateinit var fileManager: FileManager

    @Inject
    lateinit var firebaseAuth: FirebaseAuth


    private val viewModel: DetailsViewModel by viewModels()
    private lateinit var taskAdapter: DetailTaskAdapter
    private lateinit var attachmentAdapter: DetailAttachmentAdapter
    private lateinit var dragHelper: ItemTouchHelper
    private val alarmManager: AlarmManager by lazy { requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    private var todoId: String? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupTaskRv()
        setupAttachmentRv()

        binding.titleTv.onKeyboardEnter(false) { input ->
            todoId?.let {
                if (it.trim().toString().isEmpty()) {
                    viewModel.updateTodoTitle(
                        userId = firebaseAuth.currentUser!!.uid, title = input, todoId = it
                    )
                }
            }
            if (input.isEmpty()) {
                makeToast("Please set title.")
            }
        }

        binding.taskEt.onKeyboardEnter(true) { input ->
            todoId?.let {
                viewModel.insertTask(
                    userId = firebaseAuth.currentUser!!.uid,
                    title = input,
                    position = taskAdapter.currentList.size,
                    todoRefId = it
                )
                if (taskAdapter.currentList.size == 0) {
                    viewModel.updateTodoTasksAvailability(
                        userId = firebaseAuth.currentUser!!.uid,
                        todoId = it,
                        tasksAvailability = true
                    )
                }
            }
        }

        binding.dueDateTv.transformIntoDatePicker(requireContext()) { localDate ->
            binding.dueDateTv.text = getDateString(dateUtil.toLocalDateTime(localDate))
            todoId?.let {
                viewModel.updateDeadline(
                    userId = firebaseAuth.currentUser!!.uid,
                    todoId = it,
                    deadline = dateUtil.toLong(localDate)
                )
            }
            binding.dueDateTvClear.isGone = false
        }

        onClearText(binding.dueDateTvClear, binding.dueDateTv, "Due Date") {
            todoId?.let {
                viewModel.updateDeadline(
                    userId = firebaseAuth.currentUser!!.uid, todoId = it, deadline = null
                )
            }
        }

        binding.notesTv.setOnClickListener {
            makeToast("delete note tapped")
            val action = DetailsFragmentDirections.actionDetailsFragmentToNoteFragment()
            findNavController().navigate(action)
        }

        onClearText(binding.notesTvClear, binding.notesTv, "Notes") {
            todoId?.let {
                viewModel.deleteNote(userId = firebaseAuth.currentUser!!.uid, noteId = it)
            }
        }

        binding.attachmentTv.setOnClickListener {
            getFiles()
        }

        binding.categoryContainer.setOnClickListener {
            categoryPopupMenu.showCategoryPopupMenu()
        }

        collectLifecycleFlow(viewModel.todoId) {
            todoId = it
        }

        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->
            setupCategoryPopupMenu(
                binding.categoryContainer,
                uiState.categories,
                uiState.todoDetails?.todo?.todoCategoryRefName
            )
            taskAdapter.submitList(uiState.todoDetails?.tasks)
            uiState.todoDetails?.attachments?.let {
                attachmentAdapter.submitList(it)
            }

            binding.titleTv.setText(uiState.todoDetails?.todo?.title)

            binding.categoryTv.text = uiState.todoDetails?.todo?.todoCategoryRefName

            binding.dueDateTvClear.isGone = uiState.todoDetails?.todo?.deadline == null

            binding.reminderTvClear.isGone = uiState.todoDetails?.todo?.reminder == null

            binding.repeatTvClear.isGone = uiState.todoDetails?.todo?.repeat.isNullOrEmpty()

            binding.notesTvClear.isGone = uiState.todoDetails?.note == null

            binding.attachmentRv.isGone = uiState.todoDetails?.attachments.isNullOrEmpty()

            if (uiState.todoDetails?.todo?.deadline == null) {
                binding.dueDateTv.text = "Due Date"
            } else {
                binding.dueDateTv.text =
                    getDateString(dateUtil.toLocalDateTime(uiState.todoDetails.todo.deadline))
            }

            if (uiState.todoDetails?.todo?.reminder == null) {
                binding.reminderTv.text = "Reminder"
            } else {
                binding.reminderTv.text = generateReminderString(uiState.todoDetails.todo.reminder)
            }

            if (uiState.todoDetails?.note == null) {
                binding.notesTv.text = "Notes"
            } else {
                binding.notesTv.text = uiState.todoDetails.note.title
            }

            uiState.todoDetails?.todo?.createdOn?.let {
                binding.createdOnTv.text =
                    "Created on ${getDateString(dateUtil.toLocalDateTime(it))}"
            }

            uiState.todoDetails?.todo?.let { todo ->

                val isComplete = todo.isComplete
                binding.checkbox.isChecked = isComplete

                binding.checkbox.setOnClickListener {
                    viewModel.updateTodoCompletion(
                        userId = firebaseAuth.currentUser!!.uid,
                        todoId = todo.todoId,
                        isComplete = !isComplete
                    )
                }

                binding.reminderTv.setOnClickListener {
                    dialogsManager.showReminderDateTimePickerDialog { reminder ->
                        binding.reminderTv.text = generateReminderString(dateUtil.toLong(reminder))
                        viewModel.updateReminder(
                            userId = firebaseAuth.currentUser!!.uid,
                            todoId = todo.todoId,
                            reminder = dateUtil.toLong(reminder)
                        )
                        if (todo.alarmRef != null) {
                            setAlarm(todo.alarmRef, dateUtil.toLong(reminder), todo.title)
                        } else {
                            val alarmRef = nextInt()
                            viewModel.updateTodoAlarmRef(
                                userId = firebaseAuth.currentUser!!.uid,
                                todoId = todo.todoId,
                                alarmRef = alarmRef
                            )
                        }
                        binding.dueDateTvClear.isGone = false
                    }
                }

                onClearText(binding.reminderTvClear, binding.reminderTv, "Reminder") {
                    viewModel.updateReminder(
                        userId = firebaseAuth.currentUser!!.uid,
                        todoId = todo.todoId,
                        reminder = null
                    )
                    todo.alarmRef?.let {
                        cancelAlarm(it)
                        viewModel.updateTodoAlarmRef(
                            userId = firebaseAuth.currentUser!!.uid,
                            todoId = todo.todoId,
                            alarmRef = null
                        )
                    }
                }

                binding.deleteIv.setOnClickListener {
                    viewModel.deleteTodo(
                        userId = firebaseAuth.currentUser!!.uid, todoId = todo.todoId
                    )
                    todo.alarmRef?.let {
                        cancelAlarm(it)
                    }
                    uiState.todoDetails.attachments.forEach {
                        viewModel.deleteFileFromInternalStorage(it.localUri)
                    }
                    requireActivity().finish()
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
                                userId = firebaseAuth.currentUser!!.uid,
                                todoCategoryName = categoryName
                            )
                            todoId?.let {
                                viewModel.updateTodoCategory(
                                    userId = firebaseAuth.currentUser!!.uid,
                                    todoId = it,
                                    category = categoryName
                                )
                            }
                        } else {
                            makeToast("Please provide name for category")
                        }
                    }
                    true
                }
                else -> {
                    makeToast(menuItem.title.toString())
                    todoId?.let {
                        viewModel.updateTodoCategory(
                            userId = firebaseAuth.currentUser!!.uid,
                            todoId = it,
                            category = menuItem.title.toString()
                        )
                    }
                    binding.categoryTv.text = menuItem.title
                    true
                }
            }
        }
    }

    private fun setupTaskRv() {
        taskAdapter = DetailTaskAdapter(this)
        enableDragAndDrop()
        enableSwipeToDeleteAndUndo()
        binding.taskRv.adapter = taskAdapter
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback = object : SwipeController(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                // clear focus from textview because to avoid outofboundsexception.
                viewHolder.itemView.findViewById<AppCompatEditText>(R.id.task).clearFocus()
                val position = viewHolder.absoluteAdapterPosition
                val task = taskAdapter.currentList[position]
                if (taskAdapter.currentList.size == 1) {
                    task.todoRefId?.let {
                        viewModel.updateTodoTasksAvailability(
                            userId = firebaseAuth.currentUser!!.uid, todoId = it, false
                        )
                    }
                }
                viewModel.deleteTask(
                    userId = firebaseAuth.currentUser!!.uid,
                    taskId = task.taskId,
                    todoId = task.todoRefId!!
                )
                undoDeleteTask(task)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.taskRv)
    }

    private fun undoDeleteTask(task: Task) {
        Snackbar.make(binding.root, "Todo is deleted", Snackbar.LENGTH_SHORT).apply {
            setAction("Undo") {
                viewModel.restoreDeletedTask(userId = firebaseAuth.currentUser!!.uid, task)
                task.todoRefId?.let {
                    viewModel.updateTodoTasksAvailability(
                        userId = firebaseAuth.currentUser!!.uid,
                        todoId = it,
                        tasksAvailability = true
                    )
                }
            }
            setActionTextColor(Color.YELLOW)
            show()
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
            viewModel.updateTaskPosition(
                userId = firebaseAuth.currentUser!!.uid,
                taskId = task.taskId,
                position = index,
                todoId = todoId!!
            )
        }
    }

    override fun updateTaskTitle(taskId: String?, title: String) {
        viewModel.updateTaskTitle(
            userId = firebaseAuth.currentUser!!.uid,
            taskId = taskId!!,
            title = title,
            todoId = todoId!!
        )
    }

    override fun deleteTask(taskId: String) {
        viewModel.deleteTask(
            userId = firebaseAuth.currentUser!!.uid, taskId = taskId, todoId = todoId!!
        )
    }

    override fun updateTaskCompletion(taskId: String?, isComplete: Boolean) {
        viewModel.updateTaskCompletion(
            userId = firebaseAuth.currentUser!!.uid,
            taskId = taskId!!,
            isComplete = isComplete,
            todoId = todoId!!
        )
    }

    private fun onClearText(
        imageView: AppCompatImageView,
        textView: AppCompatTextView,
        text: String,
        callback: () -> Unit
    ) {
        imageView.setOnClickListener {
            textView.text = text
            imageView.isGone = true
            callback()
        }
    }

    private fun setAlarm(alarmRef: Int, reminderAt: Long, title: String) {
        val flags =
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val noCreateFlag =
            PendingIntent.FLAG_NO_CREATE or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra(TODO_ID, todoId)
        intent.putExtra(ALARM_REF, alarmRef)
        intent.putExtra(NOTIFICATION_TITLE, title)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), alarmRef, intent, flags)
        alarmManager.setExact(AlarmManager.RTC, reminderAt, pendingIntent)

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

    private fun getFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "*/*"
        val title = "Get attachment from"
        val selector = Intent.createChooser(intent, title)

        startActivityIntent.launch(selector)
    }

    private val startActivityIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data

                data?.let {
                    if (data.clipData != null) {
                        val clipData = data.clipData!!
                        val count = clipData.itemCount
                        for (i in 0 until count) {
                            val uri = clipData.getItemAt(i)?.uri
                            uri?.let {
                                val fileName = fileManager.queryName(uri)
                                Log.i("DetailFragment", "uri: $uri, name: $fileName")
                                val attachment = viewModel.createAttachment(
                                    userId = firebaseAuth.currentUser!!.uid,
                                    originalFileUri = it,
                                    size = uri.length(requireContext()),
                                    todoRefId = todoId!!
                                )

                                viewModel.insertAttachment(firebaseAuth.currentUser!!.uid, attachment)

                                Log.i("attachmentPath", "DetailsFragment - $it")
                                viewModel.uploadAttachment(
                                    userId = firebaseAuth.currentUser!!.uid,
                                    initialFileUri = it,
                                    internalStoragePath = attachment.localUri,
                                    attachmentId = attachment.attachmentId
                                )
                            }
                        }
                    } else {
                        val uri = data.data
                        uri?.let{
                            val fileName = fileManager.queryName(uri)

                            val attachment = viewModel.createAttachment(
                                userId = firebaseAuth.currentUser!!.uid,
                                originalFileUri = it,
                                size = uri.length(requireContext()),
                                todoRefId = todoId!!
                            )

                            viewModel.insertAttachment(firebaseAuth.currentUser!!.uid, attachment)

                            Log.i("attachmentPath", "DetailsFragment - $it")

                            viewModel.uploadAttachment(
                                userId = firebaseAuth.currentUser!!.uid,
                                initialFileUri = it,
                                internalStoragePath = attachment.localUri,
                                attachmentId = attachment.attachmentId
                            )
                        }
                    }
                    todoId?.let {
                        viewModel.updateTodoAttachmentsAvailability(
                            userId = firebaseAuth.currentUser!!.uid,
                            todoId = it,
                            attachmentsAvailability = true
                        )
                    }
                }
            }
        }


    private fun setupAttachmentRv() {
        attachmentAdapter = DetailAttachmentAdapter(this)
        binding.attachmentRv.adapter = attachmentAdapter
    }

    override fun openFile(attachment: Attachment) {
        val filePath = File(requireContext().filesDir, "attachments/${attachment.type}")
        val newFile = File(filePath, attachment.name)
        val contentUri = try {
            FileProvider.getUriForFile(
                requireContext(), "com.example.to_dolistclone.fileprovider", newFile
            )
        } catch (e: Exception) {
            Log.e("DetailsFragment", "openFile FileProvider: ${e.message}")
            null
        }

        contentUri?.let {
            val extension = fileManager.getExtension(it)
            val mime = fileManager.getMime(extension!!)
            val title = "View in"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(it, mime)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            intent.clipData = ClipData.newRawUri("", it)
            val selector = Intent.createChooser(intent, title)

            try {
                requireActivity().setResult(Activity.RESULT_OK, selector)
                requireActivity().startActivity(selector)
            } catch (e: Exception) {
                Log.e("DetailsFragment", "openFile - errorMsg: ${e.message}")
            }
        }

    }

    override fun deleteAttachment(attachment: Attachment) {
        if (attachmentAdapter.itemCount == 1) {
            viewModel.updateTodoAttachmentsAvailability(
                userId = firebaseAuth.currentUser!!.uid,
                todoId = attachment.todoRefId,
                attachmentsAvailability = false
            )
        }
        viewModel.deleteAttachment(
            userId = firebaseAuth.currentUser!!.uid,
            attachment = attachment,
            todoId = todoId!!,
            networkPath = attachment.networkUri
        )
    }

}