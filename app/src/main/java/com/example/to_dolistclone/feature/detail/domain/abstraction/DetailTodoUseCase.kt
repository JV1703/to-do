package com.example.to_dolistclone.feature.detail.domain.abstraction

import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.TodoDetails
import kotlinx.coroutines.flow.Flow

interface DetailTodoUseCase {

    fun getTodoDetails(todoId: String): Flow<TodoDetails?>
    suspend fun updateTodoDeadline(userId: String, todoId: String, deadline: Long?): Async<Int>
    suspend fun updateTodoReminder(userId: String, todoId: String, reminder: Long?): Async<Int>
    suspend fun updateTodoTitle(userId: String, todoId: String, title: String): Async<Int>
    suspend fun updateTodoCategory(userId: String, todoId: String, category: String): Async<Int?>
    suspend fun updateTodoCompletion(userId: String, todoId: String, isComplete: Boolean, completedOn: Long?): Async<Int>
    suspend fun deleteTodo(userId: String, todoId: String): Async<Int>
    suspend fun updateTodoTasksAvailability(userId: String, todoId: String, tasksAvailability: Boolean): Async<Int>
    suspend fun updateTodoNotesAvailability(userId: String, todoId: String, notesAvailability: Boolean): Async<Int>
    suspend fun updateTodoAttachmentsAvailability(
        userId: String,
        todoId: String,
        attachmentsAvailability: Boolean
    ): Async<Int>

    suspend fun updateTodoAlarmRef(userId: String, todoId: String, alarmRef: Int?): Async<Int>
    suspend fun saveSelectedTodoId(todoId: String)
    fun getSelectedTodoId(): Flow<String>

}