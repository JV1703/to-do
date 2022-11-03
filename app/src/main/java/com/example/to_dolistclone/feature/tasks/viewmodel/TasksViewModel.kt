package com.example.to_dolistclone.feature.tasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolistclone.core.domain.model.TodoCategory
import com.example.to_dolistclone.feature.detail.domain.abstraction.create_update.InsertTodo
import com.example.to_dolistclone.feature.detail.domain.abstraction.create_update.InsertTodoCategory
import com.example.to_dolistclone.feature.detail.domain.abstraction.read.GetTodoCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TasksUiState(
    val categories: Set<String> = emptySet(),
    val selectedCategory: String? = null
)
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val insertTodoUseCase: InsertTodo,
    private val insertTodoCategory: InsertTodoCategory,
    private val getTodoCategories: GetTodoCategories
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    val todoCategories = getTodoCategories().map {
        it.map { it.todoCategoryName }.toSet()
    }

    val uiState = combine(selectedCategory, todoCategories){ selectedCategory, todoCategories ->
        TasksUiState(
            categories = todoCategories,
            selectedCategory = selectedCategory
        )
    }

    fun updateSelectedCategory(selectedCategory: String) {
        _selectedCategory.value = selectedCategory
    }

    fun insertTodoCategory(todoCategoryName: String) {
        viewModelScope.launch {
            if (todoCategoryName.isNotEmpty()) {
                insertTodoCategory(TodoCategory(todoCategoryName))
            }
        }
    }

}