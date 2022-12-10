package com.example.to_dolistclone.feature.detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.common.FileManager
import com.example.to_dolistclone.core.domain.model.*
import com.example.to_dolistclone.feature.detail.domain.abstraction.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

data class DetailsActivityUiState(
    val categories: Set<String> = emptySet(),
    val selectedCategory: String? = null,
    val todoDetails: TodoDetails? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val detailTodoUseCase: DetailTodoUseCase,
    private val todoCategoryUseCase: TodoCategoryUseCase,
    private val detailTaskUseCase: DetailTaskUseCase,
    private val detailNoteUseCase: DetailNoteUseCase,
    private val detailAttachmentUseCase: DetailAttachmentUseCase,
    private val dateUtil: DateUtil,
    private val fileManager: FileManager
) : ViewModel() {
    val todoId = detailTodoUseCase.getSelectedTodoId()

    private val customScope = CoroutineScope(SupervisorJob())
    private var customJob: Job? = null
    private val todoDetails = todoId.flatMapLatest { detailTodoUseCase.getTodoDetails(it) }
    private val todoCategories = todoCategoryUseCase.getTodoCategories().map { todos ->
        todos.map { todo -> todo.todoCategoryName }.toSet()
    }

    val uiState = combine(
        todoCategories, todoDetails
    ) { todoCategories, todoDetails ->
        Log.i("DetailsUiState", "triggered")
        DetailsActivityUiState(
            categories = todoCategories,
            selectedCategory = todoDetails?.todo?.todoCategoryRefName,
            todoDetails = todoDetails
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailsActivityUiState()
    )

    fun insertTodoCategory(userId: String, todoCategoryName: String) {
        viewModelScope.launch {
            if (todoCategoryName.isNotEmpty()) {
                todoCategoryUseCase.insertTodoCategory(
                    userId = userId, todoCategoryName = todoCategoryName
                )
            }
        }
    }

    fun updateTodoCategory(userId: String, todoId: String, category: String) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoCategory(
                userId = userId, todoId = todoId, category = category
            )
        }
    }

//    fun updateTaskPosition(tasks: List<Task>) {
//        viewModelScope.launch {
//            detailTaskUseCase.insertTasks(tasks)
//        }
//    }

    fun insertTask(
        userId: String, taskId: String? = null, title: String, position: Int, todoRefId: String
    ) {
        viewModelScope.launch {
            val task = createTask(
                taskId = taskId, title = title, position = position, todoRefId = todoRefId
            )
            detailTaskUseCase.insertTask(userId = userId, task = task)
        }
    }

    private fun createTask(taskId: String?, title: String, position: Int, todoRefId: String) = Task(
        taskId = taskId ?: UUID.randomUUID().toString(),
        task = title,
        isComplete = false,
        position = position,
        todoRefId = todoRefId
    )

    fun deleteTask(userId: String, taskId: String) {
        viewModelScope.launch {
            detailTaskUseCase.deleteTask(userId = userId, taskId = taskId)
        }
    }

    fun updateTaskPosition(userId: String, taskId: String, position: Int) {
        viewModelScope.launch {
            detailTaskUseCase.updateTaskPosition(
                userId = userId, taskId = taskId, position = position
            )
        }
    }

    fun restoreDeletedTaskProxy(userId: String, task: Task) {
        viewModelScope.launch {
            detailTaskUseCase.insertTask(userId = userId, task = task)
        }
    }

    fun updateDeadline(userId: String, todoId: String, deadline: Long?) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoDeadline(
                userId = userId, todoId = todoId, deadline = deadline
            )
        }
    }

    fun updateReminder(userId: String, todoId: String, reminder: Long?) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoReminder(
                userId = userId, todoId = todoId, reminder = reminder
            )
        }
    }

    fun updateTaskTitle(userId: String, taskId: String, title: String) {
        viewModelScope.launch {
            detailTaskUseCase.updateTaskTitle(userId = userId, taskId = taskId, title = title)
        }
    }

    fun updateTaskCompletion(userId: String, taskId: String, isComplete: Boolean) {
        viewModelScope.launch {
            detailTaskUseCase.updateTaskCompletion(
                userId = userId, taskId = taskId, isComplete = isComplete
            )
        }
    }

    fun updateTodoTitle(userId: String, todoId: String, title: String) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoTitle(userId = userId, todoId = todoId, title = title)
        }
    }

    fun deleteTodo(userId: String, todoId: String) {
        viewModelScope.launch {
            detailTodoUseCase.deleteTodo(userId = userId, todoId = todoId)
        }
    }

    fun upsertNote(userId: String, note: Note) {
        customJob = customScope.launch {
            try {
                detailNoteUseCase.insertNote(userId = userId, note = note)
            }catch (e: Exception){
                Log.e("insertNote", e.message?:"Unknown Error")
            }finally {
//                customJob?.cancel()
                Log.i("insertNote", "job: $customJob, is job active: ${customJob?.isActive} " +
                        "coroutine: $customScope, is coroutineActive: ${customScope.isActive}")
            }
        }
    }

    fun createNote(
        noteId: String, title: String, body: String, createdAt: Long? = null
    ): Note {
        val currentDateTimeLong = dateUtil.getCurrentDateTimeLong()
        return Note(
            noteId = noteId,
            title = title,
            body = body,
            created_at = createdAt ?: currentDateTimeLong,
            updated_at = currentDateTimeLong,
        )
    }

    fun deleteNote(userId: String, noteId: String) {
        customJob = customScope.launch {
            try {
                detailNoteUseCase.deleteNote(userId = userId, noteId = noteId)
            }catch (e: Exception){
                Log.e("deleteNote", e.message?:"Unknown Error")
            }finally {
//                customJob?.cancel()
                Log.i("deleteNote", "job: $customJob, is job active: ${customJob?.isActive} " +
                        "coroutine: $customScope, is coroutineActive: ${customScope.isActive}")
            }
        }
    }

    fun updateTodoCompletion(userId: String, todoId: String, isComplete: Boolean) {
        viewModelScope.launch {
            val currentDateTimeLong = dateUtil.getCurrentDateTimeLong()
            detailTodoUseCase.updateTodoCompletion(
                userId = userId,
                todoId = todoId,
                isComplete = isComplete,
                completedOn = if (isComplete) currentDateTimeLong else null
            )
        }
    }

    fun updateTodoTasksAvailability(
        userId: String, todoId: String, tasksAvailability: Boolean
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoTasksAvailability(
                userId = userId, todoId = todoId, tasksAvailability = tasksAvailability
            )
        }
        Log.i("testing", "triggered")
    }

    fun updateTodoNotesAvailability(
        userId: String, todoId: String, notesAvailability: Boolean
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoNotesAvailability(
                userId = userId, todoId = todoId, notesAvailability = notesAvailability
            )
        }
    }

    fun updateTodoAttachmentsAvailability(
        userId: String, todoId: String, attachmentsAvailability: Boolean
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoAttachmentsAvailability(
                userId = userId, todoId = todoId, attachmentsAvailability = attachmentsAvailability
            )
        }
    }

    fun updateTodoAlarmRef(userId: String, todoId: String, alarmRef: Int?) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoAlarmRef(
                userId = userId, todoId = todoId, alarmRef = alarmRef
            )
        }
    }

    fun createAttachment(
        fileName: String, filePath: String, type: String, size: Long, todoRefId: String
    ): Attachment {
        return Attachment(
            attachmentId = UUID.randomUUID().toString(),
            name = fileName,
            uri = filePath,
            type = type,
            size = size,
            todoRefId = todoRefId
        )
    }

    fun insertAttachment(userId: String, attachment: Attachment) {
        viewModelScope.launch {
            detailAttachmentUseCase.insertAttachment(userId = userId, attachment = attachment)
        }
    }

    fun deleteAttachment(userId: String, attachment: Attachment) {
        viewModelScope.launch {
            detailAttachmentUseCase.deleteAttachment(
                userId = userId, attachmentId = attachment.attachmentId
            )
            deleteFileFromInternalStorage(attachment.uri)
        }
    }

    fun deleteFileFromInternalStorage(filePath: String) {
        viewModelScope.launch {
            fileManager.deleteFileFromInternalStorage(filePath = filePath)
        }
    }

}