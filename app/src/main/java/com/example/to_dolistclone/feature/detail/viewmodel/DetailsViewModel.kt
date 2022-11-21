package com.example.to_dolistclone.feature.detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.domain.model.*
import com.example.to_dolistclone.feature.detail.domain.abstraction.DetailTodoUseCase
import com.example.to_dolistclone.feature.detail.domain.abstraction.NoteUseCase
import com.example.to_dolistclone.feature.detail.domain.abstraction.TaskUseCase
import com.example.to_dolistclone.feature.detail.domain.abstraction.TodoCategoryUseCase
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
    private val taskUseCase: TaskUseCase,
    private val noteUseCase: NoteUseCase,
    private val dateUtil: DateUtil
) : ViewModel() {

    private val _todoId = MutableStateFlow("")
    val todoId = _todoId.asStateFlow()

    private val todoDetails = todoId.flatMapLatest { detailTodoUseCase.getTodoDetails(it) }
    private val todoCategories = todoCategoryUseCase.getTodoCategories().map {
        it.map { it.todoCategoryName }.toSet()
    }

    val uiState = combine(
        todoCategories, todoDetails
    ) { todoCategories, todoDetails ->
        DetailsActivityUiState(
            categories = todoCategories,
            selectedCategory = todoDetails?.todo?.todoCategoryRefName,
            todoDetails = todoDetails
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), DetailsActivityUiState()
    )

    fun saveTodoId(todoId: String) {
        this@DetailsViewModel._todoId.value = todoId
    }

    fun insertTodoCategory(todoCategoryName: String) {
        viewModelScope.launch {
            if (todoCategoryName.isNotEmpty()) {
                todoCategoryUseCase.insertTodoCategory(TodoCategory(todoCategoryName))
            }
        }
    }

    fun updateTodoCategory(category: String) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoCategory(todoId.value, category)
        }
    }

    fun updateTaskPosition(tasks: List<Task>) {
        viewModelScope.launch {
            taskUseCase.insertTasks(tasks)
        }
    }

    fun insertTask(taskId: String? = null, title: String, position: Int) {
        viewModelScope.launch {
            val task = createTask(taskId, title, position)
            taskUseCase.insertTask(task)
        }
    }

    private fun createTask(taskId: String?, title: String, position: Int) = Task(
        taskId = taskId ?: UUID.randomUUID().toString(),
        task = title,
        isComplete = false,
        position = position,
        todoRefId = todoId.value
    )

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            taskUseCase.deleteTask(taskId)
        }
    }

    fun updateTaskPosition(taskId: String, position: Int) {
        viewModelScope.launch {
            taskUseCase.updateTaskPosition(taskId, position)
        }
    }

    fun restoreDeletedTaskProxy(task: Task) {
        viewModelScope.launch {
            taskUseCase.insertTask(task)
        }
    }

    fun updateDeadline(deadline: Long?) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoDeadline(todoId.value, deadline)
        }
    }

    fun updateReminder(reminder: Long?) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoReminder(todoId.value, reminder)
        }
    }

    fun updateTaskTitle(taskId: String, title: String) {
        viewModelScope.launch {
            taskUseCase.updateTaskTitle(taskId, title)
        }
    }

    fun updateTaskCompletion(taskId: String, isComplete: Boolean) {
        viewModelScope.launch {
            taskUseCase.updateTaskCompletion(taskId, isComplete)
        }
    }

    fun updateTodoTitle(title: String) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoTitle(this@DetailsViewModel.todoId.value, title)
        }
    }

    fun deleteTodo(){
        viewModelScope.launch {
            detailTodoUseCase.deleteTodo(todoId.value)
        }
    }

    fun insertNote(note: Note) {
        viewModelScope.launch {
            noteUseCase.insertNote(note)
        }
    }

    fun createNote(
        title: String, body: String, createdAt: Long? = null
    ): Note {
        val currentDateTimeLong = dateUtil.getCurrentDateTimeLong()
        return Note(
            noteId = todoId.value,
            title = title,
            body = body,
            created_at = createdAt ?: currentDateTimeLong,
            updated_at = currentDateTimeLong,
        )
    }
    
    fun deleteNote(){
        viewModelScope.launch { 
            noteUseCase.deleteNote(todoId.value)
        }
    }

    fun updateTodoCompletion(isComplete: Boolean) {
        viewModelScope.launch {
            val currentDateTimeLong = dateUtil.getCurrentDateTimeLong()
            detailTodoUseCase.updateTodoCompletion(
                todoId.value,
                isComplete = isComplete,
                completedOn = if (isComplete) currentDateTimeLong else null
            )
        }
    }

    fun updateTodoTasksAvailability(
        tasksAvailability: Boolean
    ) {
        viewModelScope.launch {
            detailTodoUseCase.updateTodoTasksAvailability(todoId.value, tasksAvailability)
        }
        Log.i("testing","triggered")
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

}