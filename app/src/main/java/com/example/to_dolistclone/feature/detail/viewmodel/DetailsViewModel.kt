package com.example.to_dolistclone.feature.detail.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.common.FileManager
import com.example.to_dolistclone.core.domain.model.*
import com.example.to_dolistclone.feature.detail.domain.abstraction.*
import com.google.firebase.storage.FirebaseStorage
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
    private val fileManager: FileManager,
    private val firebaseStorage: FirebaseStorage
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

    fun updateTodoCategory(
        userId: String,
        todoId: String,
        category: String,
        updatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoCategory(
                userId = userId, todoId = todoId, category = category, updatedOn = updatedOn
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
            detailTaskUseCase.insertTask(
                userId = userId, task = task, todoUpdatedOn = dateUtil.getCurrentDateTimeLong()
            )
        }
    }

    private fun createTask(taskId: String?, title: String, position: Int, todoRefId: String) = Task(
        taskId = taskId ?: UUID.randomUUID().toString(),
        task = title,
        isComplete = false,
        position = position,
        todoRefId = todoRefId
    )

    fun deleteTask(
        userId: String,
        taskId: String,
        todoId: String,
        todoUpdatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            detailTaskUseCase.deleteTask(
                userId = userId, taskId = taskId, todoId = todoId, todoUpdatedOn = todoUpdatedOn
            )
        }
    }

    fun updateTaskPosition(
        userId: String,
        taskId: String,
        position: Int,
        todoId: String,
        todoUpdatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            detailTaskUseCase.updateTaskPosition(
                userId = userId,
                taskId = taskId,
                position = position,
                todoId = todoId,
                todoUpdatedOn = todoUpdatedOn
            )
        }
    }

    fun restoreDeletedTask(
        userId: String, task: Task, todoUpdatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            detailTaskUseCase.insertTask(
                userId = userId, task = task, todoUpdatedOn = todoUpdatedOn
            )
        }
    }

    fun updateDeadline(
        userId: String,
        todoId: String,
        deadline: Long?,
        updatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoDeadline(
                userId = userId, todoId = todoId, deadline = deadline, updatedOn = updatedOn
            )
        }
    }

    fun updateReminder(
        userId: String,
        todoId: String,
        reminder: Long?,
        updatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoReminder(
                userId = userId, todoId = todoId, reminder = reminder, updatedOn = updatedOn
            )
        }
    }

    fun updateTaskTitle(
        userId: String,
        taskId: String,
        title: String,
        todoId: String,
        todoUpdatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            detailTaskUseCase.updateTaskTitle(
                userId = userId,
                taskId = taskId,
                title = title,
                todoId = todoId,
                todoUpdatedOn = todoUpdatedOn
            )
        }
    }

    fun updateTaskCompletion(
        userId: String,
        taskId: String,
        isComplete: Boolean,
        todoId: String,
        todoUpdatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            detailTaskUseCase.updateTaskCompletion(
                userId = userId,
                taskId = taskId,
                isComplete = isComplete,
                todoId = todoId,
                todoUpdatedOn = todoUpdatedOn
            )
        }
    }

    fun updateTodoTitle(
        userId: String,
        todoId: String,
        title: String,
        updatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoTitle(
                userId = userId, todoId = todoId, title = title, updatedOn = updatedOn
            )
        }
    }

    fun deleteTodo(userId: String, todoId: String) {
        viewModelScope.launch {
            detailTodoUseCase.deleteTodo(userId = userId, todoId = todoId)
        }
    }

    fun upsertNote(
        userId: String, note: Note, todoUpdatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        customJob = customScope.launch {
            try {
                detailNoteUseCase.insertNote(
                    userId = userId, note = note, todoUpdatedOn = todoUpdatedOn
                )
            } catch (e: Exception) {
                Log.e("insertNote", e.message ?: "Unknown Error")
            } finally {
//                customJob?.cancel()
                Log.i(
                    "insertNote",
                    "job: $customJob, is job active: ${customJob?.isActive} " + "coroutine: $customScope, is coroutineActive: ${customScope.isActive}"
                )
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

    fun deleteNote(
        userId: String, noteId: String, todoUpdatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        customJob = customScope.launch {
            try {
                detailNoteUseCase.deleteNote(
                    userId = userId, noteId = noteId, todoUpdatedOn = todoUpdatedOn
                )
                detailTodoUseCase.updateTodoNotesAvailability(
                    userId = userId,
                    todoId = noteId,
                    notesAvailability = false,
                    updatedOn = todoUpdatedOn
                )
            } catch (e: Exception) {
                Log.e("deleteNote", e.message ?: "Unknown Error")
            } finally {
//                customJob?.cancel()
                Log.i(
                    "deleteNote",
                    "job: $customJob, is job active: ${customJob?.isActive} " + "coroutine: $customScope, is coroutineActive: ${customScope.isActive}"
                )
            }
        }
    }

    fun updateTodoCompletion(
        userId: String,
        todoId: String,
        isComplete: Boolean,
        updatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            val currentDateTimeLong = dateUtil.getCurrentDateTimeLong()
            detailTodoUseCase.updateTodoCompletion(
                userId = userId,
                todoId = todoId,
                isComplete = isComplete,
                completedOn = if (isComplete) currentDateTimeLong else null,
                updatedOn = updatedOn
            )
        }
    }

    fun updateTodoTasksAvailability(
        userId: String,
        todoId: String,
        tasksAvailability: Boolean,
        updatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoTasksAvailability(
                userId = userId,
                todoId = todoId,
                tasksAvailability = tasksAvailability,
                updatedOn = updatedOn
            )
        }
        Log.i("testing", "triggered")
    }

    fun updateTodoNotesAvailability(
        userId: String,
        todoId: String,
        notesAvailability: Boolean,
        updatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoNotesAvailability(
                userId = userId,
                todoId = todoId,
                notesAvailability = notesAvailability,
                updatedOn = updatedOn
            )
        }
    }

    fun updateTodoAttachmentsAvailability(
        userId: String,
        todoId: String,
        attachmentsAvailability: Boolean,
        updatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoAttachmentsAvailability(
                userId = userId,
                todoId = todoId,
                attachmentsAvailability = attachmentsAvailability,
                updatedOn
            )
        }
    }

    fun updateTodoAlarmRef(
        userId: String,
        todoId: String,
        alarmRef: Int?,
        updatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoAlarmRef(
                userId = userId, todoId = todoId, alarmRef = alarmRef, updatedOn = updatedOn
            )
        }
    }

    fun createAttachment(
        userId: String, originalFileUri: Uri, size: Long, todoRefId: String
    ): Attachment {

        val fileName = fileManager.queryName(originalFileUri)
        val localUri = fileManager.generateInternalStorageDestination(originalFileUri)
        val fileType = fileManager.getExtension(originalFileUri)!!
        val networkUri = fileManager.generateNetworkStorageDestination(
            userId = userId, uri = originalFileUri
        )

        return Attachment(
            attachmentId = UUID.randomUUID().toString(),
            name = fileName,
            localUri = localUri,
            networkUri = networkUri,
            type = fileType,
            size = size,
            todoRefId = todoRefId
        )
    }

    fun insertAttachment(
        userId: String,
        attachment: Attachment,
        todoUpdatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {
        viewModelScope.launch {
            detailAttachmentUseCase.insertAttachment(
                userId = userId, attachment = attachment, todoUpdatedOn = todoUpdatedOn
            )
        }
    }

    fun uploadAttachment(
        userId: String,
        initialFileUri: Uri,
        internalStoragePath: String,
        attachmentId: String,
        todoUpdatedOn: Long = dateUtil.getCurrentDateTimeLong()
    ) {

        Log.i("attachmentPath", "DetailsViewModel - $initialFileUri")
        viewModelScope.launch {
            detailAttachmentUseCase.uploadAttachment(
                userId = userId,
                initialFileUri = initialFileUri,
                internalStoragePath = internalStoragePath,
                attachmentId = attachmentId,
                todoUpdatedOn = todoUpdatedOn
            )
        }
    }

    fun deleteAttachment(
        userId: String,
        attachment: Attachment,
        todoId: String,
        todoUpdatedOn: Long = dateUtil.getCurrentDateTimeLong(),
        networkPath: String
    ) {
        viewModelScope.launch {
            detailAttachmentUseCase.deleteAttachment(
                userId = userId,
                attachmentId = attachment.attachmentId,
                todoId = todoId,
                todoUpdatedOn = todoUpdatedOn,
                networkPath = networkPath
            )
            deleteFileFromInternalStorage(attachment.localUri)
        }
    }

    fun deleteFileFromInternalStorage(filePath: String) {
        viewModelScope.launch {
            fileManager.deleteFileFromInternalStorage(filePath = filePath)
        }
    }

}