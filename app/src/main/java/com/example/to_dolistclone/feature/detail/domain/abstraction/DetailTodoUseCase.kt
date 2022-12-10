package com.example.to_dolistclone.feature.detail.domain.abstraction

import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.TodoDetails
import kotlinx.coroutines.flow.Flow

interface DetailTodoUseCase {

    fun getTodoDetails(todoId: String): Flow<TodoDetails?>
    suspend fun updateTodoDeadline(userId: String, todoId: String, deadline: Long?, updatedOn: Long): Async<Int>
    suspend fun updateTodoReminder(userId: String, todoId: String, reminder: Long?, updatedOn: Long): Async<Int>
    suspend fun updateTodoTitle(userId: String, todoId: String, title: String, updatedOn: Long): Async<Int>
    suspend fun updateTodoCategory(userId: String, todoId: String, category: String, updatedOn: Long): Async<Int?>
    suspend fun updateTodoCompletion(userId: String, todoId: String, isComplete: Boolean, completedOn: Long?, updatedOn: Long): Async<Int>
    suspend fun deleteTodo(userId: String, todoId: String): Async<Int>
    suspend fun updateTodoTasksAvailability(userId: String, todoId: String, tasksAvailability: Boolean, updatedOn: Long): Async<Int>
    suspend fun updateTodoNotesAvailability(userId: String, todoId: String, notesAvailability: Boolean, updatedOn: Long): Async<Int>
    suspend fun updateTodoAttachmentsAvailability(
        userId: String,
        todoId: String,
        attachmentsAvailability: Boolean, updatedOn: Long
    ): Async<Int>

    suspend fun updateTodoAlarmRef(userId: String, todoId: String, alarmRef: Int?, updatedOn: Long): Async<Int>
    suspend fun saveSelectedTodoId(todoId: String)
    fun getSelectedTodoId(): Flow<String>

    suspend fun updateTodoUpdatedOn(userId: String, todoId: String, updatedOn: Long): Async<Int>
}