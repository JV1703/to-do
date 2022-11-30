package com.example.to_dolistclone.feature.todo_shortcut

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.feature.home.adapter.TaskProxy
import com.example.to_dolistclone.feature.home.adapter.toTask
import com.example.to_dolistclone.feature.todo_shortcut.domain.implementation.TodoShortcutUseCaseImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class TodoShortcutUiState(
    val categories: Set<String> = emptySet(), val selectedCategory: String? = null
)

@HiltViewModel
class TodoShortcutViewModel @Inject constructor(
    private val useCase: TodoShortcutUseCaseImpl
): ViewModel() {

    private val selectedCategory = MutableStateFlow<String?>(null)
    private val todoCategories = useCase.getTodoCategories().map { todoCategories ->
        todoCategories.map { todoCategory ->
            todoCategory.todoCategoryName }.toSet()
    }

    val uiState =
        combine(selectedCategory, todoCategories) { selectedCategory, todoCategories ->
            TodoShortcutUiState(
                categories = todoCategories, selectedCategory = selectedCategory
            )
        }

    fun insertTodo(tasksProxy: List<TaskProxy>, todo: Todo) {
        if (todo.title.isNotEmpty()) {
            viewModelScope.launch {
                useCase.insertTodo(todo)
                insertTasks(tasksProxy = tasksProxy, todoRefId = todo.todoId)
            }
        }
    }

    suspend fun insertTasks(tasksProxy: List<TaskProxy>, todoRefId: String) {
        if (tasksProxy.isNotEmpty()) {
            useCase.insertTasks(tasksProxy.map { it.toTask(todoRefId) })
        }
    }

    fun insertTodoCategory(categoryName: String){
        viewModelScope.launch {
            useCase.insertTodoCategory(categoryName)
        }
    }

    fun updateSelectedCategory(selectedCategory: String) {
        this@TodoShortcutViewModel.selectedCategory.value = selectedCategory
    }

    fun createTodo(
        todoId: String,
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
        alarmRef: Int,
        todoCategoryRefName: String
    ) = Todo(
        todoId = todoId,
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
        alarmRef = alarmRef,
        todoCategoryRefName = todoCategoryRefName
    )

}