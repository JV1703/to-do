package com.example.to_dolistclone.feature.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.domain.model.TodoCategory
import com.example.to_dolistclone.feature.detail.domain.abstraction.TaskUseCase
import com.example.to_dolistclone.feature.detail.domain.abstraction.TodoCategoryUseCase
import com.example.to_dolistclone.feature.home.adapter.TaskProxy
import com.example.to_dolistclone.feature.home.adapter.toTask
import com.example.to_dolistclone.feature.todo.domain.implementation.TodoUseCaseImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class TasksUiState(
    val categories: Set<String> = emptySet(), val selectedCategory: String? = null
)

data class TodoFragmentUiState(
    val showPrevious: Boolean = true,
    val showToday: Boolean = true,
    val showFuture: Boolean = true,
    val showCompletedToday: Boolean = true,
    val previousTodos: List<Todo> = emptyList(),
    val todayTodos: List<Todo> = emptyList(),
    val futureTodos: List<Todo> = emptyList(),
    val completedTodayTodos: List<Todo> = emptyList()
)

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoCategoryUseCase: TodoCategoryUseCase,
    private val taskUseCase: TaskUseCase,
    private val taskTodoUseCase: TodoUseCaseImpl
) : ViewModel() {

    private val selectedCategory = MutableStateFlow<String?>(null)

    private val todos = taskTodoUseCase.getMappedTodos()
    private val showStatus = taskTodoUseCase.getShowStatus()


    private val todoCategories = todoCategoryUseCase.getTodoCategories().map { todoCategories ->
        todoCategories.map { todoCategory ->
            todoCategory.todoCategoryName }.toSet()
    }

    val taskUiState =
        combine(selectedCategory, todoCategories) { selectedCategory, todoCategories ->
            TasksUiState(
                categories = todoCategories, selectedCategory = selectedCategory
            )
        }

    val todoFragmentUiState = combine(
        todos, showStatus
    ) { todos, showStatus ->
        TodoFragmentUiState(
            showPrevious = showStatus["previous"] ?: true,
            showToday = showStatus["today"] ?: true,
            showFuture = showStatus["future"] ?: true,
            showCompletedToday = showStatus["completedToday"] ?: true,
            previousTodos = todos["previous"] ?: emptyList(),
            todayTodos = todos["today"] ?: emptyList(),
            futureTodos = todos["future"] ?: emptyList(),
            completedTodayTodos = todos["completedToday"] ?: emptyList()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodoFragmentUiState()
    )

    fun insertTodo(tasksProxy: List<TaskProxy>, todo: Todo) {
        if (todo.title.isNotEmpty()) {
            viewModelScope.launch {
                taskTodoUseCase.insertTodo(todo)
                insertTasks(tasksProxy = tasksProxy, todoRefId = todo.todoId)
            }
        }
    }

    fun insertTodo(todo: Todo) {
        viewModelScope.launch {
            taskTodoUseCase.insertTodo(todo)
        }
    }

    suspend fun insertTasks(tasksProxy: List<TaskProxy>, todoRefId: String) {
        if (tasksProxy.isNotEmpty()) {
            taskUseCase.insertTasks(tasksProxy.map { it.toTask(todoRefId) })
        }
    }

    fun updateSelectedCategory(selectedCategory: String) {
        this@TodoViewModel.selectedCategory.value = selectedCategory
    }

    fun insertTodoCategory(todoCategoryName: String) {
        viewModelScope.launch {
            if (todoCategoryName.isNotEmpty()) {
                todoCategoryUseCase.insertTodoCategory(TodoCategory(todoCategoryName))
            }
        }
    }

    fun saveShowPrevious(isShow: Boolean) {
        viewModelScope.launch {
            taskTodoUseCase.saveShowPrevious(isShow)
        }
    }

    fun saveShowToday(isShow: Boolean) {
        viewModelScope.launch {
            taskTodoUseCase.saveShowToday(isShow)
        }
    }

    fun saveShowFuture(isShow: Boolean) {
        viewModelScope.launch {
            taskTodoUseCase.saveShowFuture(isShow)
        }
    }

    fun saveShowCompletedToday(isShow: Boolean) {
        viewModelScope.launch {
            taskTodoUseCase.saveShowCompletedToday(isShow)
        }
    }

    fun createTodo(
        title: String,
        deadline: Long?,
        reminder: Long?,
        repeat: String?,
        isComplete: Boolean,
        createdOn: Long?,
        completedOn: Long?,
        tasks: Boolean,
        notes: Boolean,
        attachments: Boolean,
        todoCategoryRefName: String
    ) = Todo(
        todoId = UUID.randomUUID().toString(),
        title = title,
        deadline = deadline,
        reminder = reminder,
        repeat = repeat,
        isComplete = isComplete,
        createdOn = createdOn,
        completedOn = completedOn,
        tasks = tasks,
        notes = notes,
        attachments = attachments,
        todoCategoryRefName = todoCategoryRefName
    )

}