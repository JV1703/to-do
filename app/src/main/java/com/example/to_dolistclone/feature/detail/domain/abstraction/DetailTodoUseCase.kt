package com.example.to_dolistclone.feature.detail.domain.abstraction

import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.domain.model.TodoDetails
import kotlinx.coroutines.flow.Flow

interface DetailTodoUseCase {

    fun getTodoDetails(todoId: String): Flow<TodoDetails?>
    suspend fun updateTodoDeadline(todoId: String, deadline: Long?): Int
    suspend fun updateTodoReminder(todoId: String, reminder: Long?): Int
    suspend fun updateTodoTitle(todoId: String, title: String): Int
    suspend fun updateTodoCategory(todoId: String, category: String): Int
    suspend fun updateTodoCompletion(todoId: String, isComplete: Boolean, completedOn: Long?): Int
    suspend fun deleteTodo(todoId: String): Int
    suspend fun updateTodoTasksAvailability(todoId: String, tasksAvailability: Boolean): Int
    suspend fun updateTodoNotesAvailability(todoId: String, notesAvailability: Boolean): Int
    suspend fun updateTodoAttachmentsAvailability(todoId: String, attachmentsAvailability: Boolean): Int
    suspend fun updateTodoAlarmRef(todoId: String, alarmRef: Int?): Int
    suspend fun saveSelectedTodoId(todoId: String)
    fun getSelectedTodoId(): Flow<String>

}