package com.example.to_dolistclone.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolistclone.feature.home.domain.abstraction.HomeTodoCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class DrawerUiState(
    val hide: Boolean = true, val categories: List<String> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val todoCategoryUseCase: HomeTodoCategoryUseCase,
) : ViewModel() {

    private val _hideCategories = MutableStateFlow(true)
    private val hideCategories = _hideCategories.asStateFlow()

    val drawerUiState = combine(
        hideCategories, todoCategoryUseCase.getTodoCategoriesName()
    ) { hide: Boolean, categories: List<String> ->
        DrawerUiState(hide = hide, categories = categories)
    }.stateIn(
        scope = viewModelScope, started = WhileSubscribed(5000), initialValue = DrawerUiState()
    )

    fun drawerRvToggle() {
        _hideCategories.value = !_hideCategories.value
    }

    fun insertTodoCategory(categoryName: String) {
        viewModelScope.launch {
            todoCategoryUseCase.insertTodoCategory(categoryName)
        }
    }

}