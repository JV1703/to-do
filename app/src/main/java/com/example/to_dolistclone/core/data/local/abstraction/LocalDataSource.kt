package com.example.to_dolistclone.core.data.local.abstraction

import com.example.to_dolistclone.core.data.local.model.*
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoCategoryWithTodosEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoDetailsEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoWithAttachmentsEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoWithTasksEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_one.TodoAndNoteEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    suspend fun saveShowPrevious(isShow: Boolean)

    suspend fun saveShowToday(isShow: Boolean)

    suspend fun saveShowFuture(isShow: Boolean)

    suspend fun saveShowCompletedToday(isShow: Boolean)

    fun getShowPrevious(): Flow<Boolean>

    fun getShowToday(): Flow<Boolean>

    fun getShowFuture(): Flow<Boolean>

    fun getShowCompletedToday(): Flow<Boolean>

    suspend fun insertTodo(todo: TodoEntity): Long

    suspend fun updateTodoTitle(todoId: String, title: String): Int

    suspend fun updateTodoCategory(todoId: String, category: String): Int

    fun getTodo(todoId: String): Flow<TodoEntity>

    suspend fun updateTodoDeadline(todoId: String, deadline: Long?): Int

    suspend fun updateTodoReminder(todoId: String, reminder: Long?): Int

    suspend fun updateTodoCompletion(todoId: String, isComplete: Boolean, completedOn: Long?): Int

    suspend fun updateTodoTasksAvailability(todoId: String, tasksAvailability: Boolean): Int

    suspend fun updateTodoNotesAvailability(todoId: String, notesAvailability: Boolean): Int

    suspend fun updateTodoAttachmentsAvailability(todoId: String, attachmentsAvailability: Boolean): Int

    suspend fun updateTodoAlarmRef(todoId: String, alarmRef: Int?): Int

    fun getTodoDetails(todoId: String): Flow<TodoDetailsEntity?>

    fun getTodos(): Flow<List<TodoEntity>>

    fun getTodos(from: Long, to: Long): Flow<List<TodoEntity>>

    suspend fun deleteTodo(todoId: String): Int

    suspend fun insertNote(note: NoteEntity): Long

    fun getNotes(): Flow<List<NoteEntity>>

    suspend fun deleteNote(noteId: String): Int

    suspend fun insertTask(task: TaskEntity): Long

    suspend fun getTaskSize(): Int

    suspend fun updateTaskPosition(taskId: String, position: Int): Int

    suspend fun updateTaskTitle(taskId: String, title: String): Int

    suspend fun updateTaskCompletion(taskId: String, isComplete: Boolean): Int

    suspend fun insertTasks(tasks: List<TaskEntity>): LongArray

    fun getTasks(): Flow<List<TaskEntity>>

    suspend fun deleteTask(taskId: String): Int

    suspend fun insertAttachment(attachment: AttachmentEntity): Long

    suspend fun insertAttachments(attachments: List<AttachmentEntity>): LongArray

    fun getAttachments(): Flow<List<AttachmentEntity>>

    suspend fun deleteAttachment(attachmentId: String): Int

    suspend fun insertTodoCategory(todoCategory: TodoCategoryEntity): Long

    fun getTodoCategories(): Flow<List<TodoCategoryEntity>>

    suspend fun deleteTodoCategory(todoCategoryName: String): Int

    fun getTodoAndNoteWithTodoId(todoId: String): Flow<TodoAndNoteEntity?>

    fun getTodoWithTasks(todoId: String): Flow<TodoWithTasksEntity?>

    fun getTodoWithAttachments(todoId: String): Flow<TodoWithAttachmentsEntity?>

    fun getTodoCategoryWithTodos(todoCategoryName: String): Flow<TodoCategoryWithTodosEntity?>

    fun getTodoCategoriesWithTodos(): Flow<List<TodoCategoryWithTodosEntity>>

    suspend fun saveSelectedTodoId(todoId: String)

    fun getSelectedTodoId(): Flow<String>

    fun getSelectedPieGraphOption(): Flow<Int>

    suspend fun saveSelectedPieGraphOption(selectedOption: Int)
}