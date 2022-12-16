package com.example.to_dolistclone.feature.detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.common.FileManager
import com.example.to_dolistclone.core.domain.model.*
import com.example.to_dolistclone.feature.detail.domain.abstraction.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
        viewModelScope, SharingStarted.WhileSubscribed(5000), DetailsActivityUiState()
    )

    fun insertTodoCategory(todoCategoryName: String) {
        viewModelScope.launch {
            if (todoCategoryName.isNotEmpty()) {
                todoCategoryUseCase.insertTodoCategory(todoCategoryName)
            }
        }
    }

    fun updateTodoCategory(todoId: String, category: String) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoCategory(todoId, category)
        }
    }

    fun updateTaskPosition(tasks: List<Task>) {
        viewModelScope.launch {
            detailTaskUseCase.insertTasks(tasks)
        }
    }

    fun insertTask(taskId: String? = null, title: String, position: Int, todoRefId: String) {
        viewModelScope.launch {
            val task = createTask(taskId, title, position, todoRefId)
            detailTaskUseCase.insertTask(task)
        }
    }

    private fun createTask(taskId: String?, title: String, position: Int, todoRefId: String) = Task(
        taskId = taskId ?: UUID.randomUUID().toString(),
        task = title,
        isComplete = false,
        position = position,
        todoRefId = todoRefId
    )

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            detailTaskUseCase.deleteTask(taskId)
        }
    }

    fun updateTaskPosition(taskId: String, position: Int) {
        viewModelScope.launch {
            detailTaskUseCase.updateTaskPosition(taskId, position)
        }
    }

    fun restoreDeletedTaskProxy(task: Task) {
        viewModelScope.launch {
            detailTaskUseCase.insertTask(task)
        }
    }

    fun updateDeadline(todoId: String, deadline: Long?) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoDeadline(todoId, deadline)
        }
    }

    fun updateReminder(todoId: String, reminder: Long?) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoReminder(todoId, reminder)
        }
    }

    fun updateTaskTitle(taskId: String, title: String) {
        viewModelScope.launch {
            detailTaskUseCase.updateTaskTitle(taskId, title)
        }
    }

    fun updateTaskCompletion(taskId: String, isComplete: Boolean) {
        viewModelScope.launch {
            detailTaskUseCase.updateTaskCompletion(taskId, isComplete)
        }
    }

    fun updateTodoTitle(todoId: String, title: String) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoTitle(todoId, title)
        }
    }

    fun deleteTodo(todoId: String) {
        viewModelScope.launch {
            detailTodoUseCase.deleteTodo(todoId)
        }
    }

    fun insertNote(note: Note) {
        viewModelScope.launch {
            detailNoteUseCase.insertNote(note)
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

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            detailNoteUseCase.deleteNote(noteId)
        }
    }

    fun updateTodoCompletion(todoId: String, isComplete: Boolean) {
        viewModelScope.launch {
            val currentDateTimeLong = dateUtil.getCurrentDateTimeLong()
            detailTodoUseCase.updateTodoCompletion(
                todoId,
                isComplete = isComplete,
                completedOn = if (isComplete) currentDateTimeLong else null
            )
        }
    }

    fun updateTodoTasksAvailability(
        todoId: String, tasksAvailability: Boolean
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoTasksAvailability(todoId, tasksAvailability)
        }
        Log.i("testing", "triggered")
    }

    fun updateTodoNotesAvailability(
        todoId: String, notesAvailability: Boolean
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoNotesAvailability(todoId, notesAvailability)
        }
    }

    fun updateTodoAttachmentsAvailability(
        todoId: String, attachmentsAvailability: Boolean
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoAttachmentsAvailability(todoId, attachmentsAvailability)
        }
    }

    fun updateTodoAlarmRef(todoId: String, alarmRef: Int?) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoAlarmRef(todoId, alarmRef)
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

    fun insertAttachment(attachment: Attachment) {
        viewModelScope.launch {
            detailAttachmentUseCase.insertAttachment(attachment)
        }
    }

    fun deleteAttachment(attachment: Attachment) {
        viewModelScope.launch {
            detailAttachmentUseCase.deleteAttachment(attachmentId = attachment.attachmentId)
            deleteFileFromInternalStorage(attachment.uri)
        }
    }

    fun deleteFileFromInternalStorage(filePath: String) {
        viewModelScope.launch {
            fileManager.deleteFileFromInternalStorage(filePath)
        }
    }

}