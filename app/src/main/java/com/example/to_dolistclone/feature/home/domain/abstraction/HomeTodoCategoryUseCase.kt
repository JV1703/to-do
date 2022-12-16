package com.example.to_dolistclone.feature.home.domain.abstraction

import com.example.to_dolistclone.core.domain.Async
import kotlinx.coroutines.flow.Flow

interface HomeTodoCategoryUseCase {

    fun getTodoCategoriesName(): Flow<List<String>>
    suspend fun insertTodoCategory(userId: String, todoCategoryName: String): Long

}