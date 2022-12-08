package com.example.to_dolistclone.core.data.remote.firebase.abstraction

import com.example.to_dolistclone.core.data.remote.model.TodoNetwork

interface TodoFirestore {

    suspend fun insertTodo(userId: String, todo: TodoNetwork)

    suspend fun getTodos(userId: String): List<TodoNetwork>

    suspend fun deleteTodo(userId: String, todoId: String)

    suspend fun updateTodoTitle(userId: String, todoId: String, title: String)

    suspend fun updateTodoCategory(userId: String, todoId: String, todoCategoryRefName: String)

    suspend fun updateTodoDeadline(userId: String, todoId: String, deadline: Long?)

    suspend fun updateTodoReminder(userId: String, todoId: String, reminder: Long?)

    suspend fun updateTodoCompletion(
        userId: String,
        todoId: String,
        isComplete: Boolean,
        completedOn: Long?
    )

    suspend fun updateTodoTasksAvailability(
        userId: String,
        todoId: String,
        tasksAvailability: Boolean
    )

    suspend fun updateTodoNotesAvailability(
        userId: String,
        todoId: String,
        notesAvailability: Boolean
    )

    suspend fun updateTodoAttachmentsAvailability(
        userId: String, todoId: String,
        attachmentsAvailability: Boolean
    )

    suspend fun updateTodoAlarmRef(userId: String, todoId: String, alarmRef: Int?)
}