package com.example.to_dolistclone.feature.detail.domain.implementation

import android.util.Log
import com.example.to_dolistclone.core.domain.model.TodoDetails
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.common.domain.todo.BaseTodoUseCaseImpl
import com.example.to_dolistclone.feature.detail.domain.abstraction.DetailTodoUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DetailTodoUseCaseImpl @Inject constructor(private val todoRepository: TodoRepository) : BaseTodoUseCaseImpl(todoRepository),
    DetailTodoUseCase {

    override fun getTodoDetails(todoId: String): Flow<TodoDetails?> {
        Log.i("getTodoDetails", "triggered")
        return todoRepository.getTodoDetails(todoId)
    }

    override suspend fun updateTodoDeadline(todoId: String, deadline: Long?): Int =
        todoRepository.updateTodoDeadline(todoId, deadline)

    override suspend fun updateTodoReminder(todoId: String, reminder: Long?): Int =
        todoRepository.updateTodoReminder(todoId, reminder)

    override suspend fun updateTodoTitle(todoId: String, title: String): Int =
        todoRepository.updateTodoCategory(todoId, title)

    override suspend fun updateTodoCategory(todoId: String, category: String): Int =
        todoRepository.updateTodoCategory(todoId, category)

    override suspend fun updateTodoCompletion(
        todoId: String, isComplete: Boolean, completedOn: Long?
    ): Int = todoRepository.updateTodoCompletion(todoId, isComplete, completedOn)

    override suspend fun updateTodoTasksAvailability(
        todoId: String, tasksAvailability: Boolean
    ): Int = todoRepository.updateTodoTasksAvailability(todoId, tasksAvailability)

    override suspend fun updateTodoNotesAvailability(
        todoId: String, notesAvailability: Boolean
    ): Int = todoRepository.updateTodoNotesAvailability(todoId, notesAvailability)

    override suspend fun updateTodoAttachmentsAvailability(
        todoId: String, attachmentsAvailability: Boolean
    ): Int = todoRepository.updateTodoAttachmentsAvailability(todoId, attachmentsAvailability)

    override suspend fun updateTodoAlarmRef(todoId: String, alarmRef: Int?): Int = todoRepository.updateTodoAlarmRef(todoId, alarmRef)

    override suspend fun deleteTodo(todoId: String): Int = todoRepository.deleteTodo(todoId)

}