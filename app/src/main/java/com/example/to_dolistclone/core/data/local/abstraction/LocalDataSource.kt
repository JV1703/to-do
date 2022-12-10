package com.example.to_dolistclone.core.data.local.abstraction

import com.example.to_dolistclone.core.data.local.CacheResult
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

    suspend fun insertTodo(todo: TodoEntity): CacheResult<Long?>

    suspend fun updateTodoTitle(todoId: String, title: String, updatedOn: Long): CacheResult<Int?>

    suspend fun updateTodoCategory(todoId: String, category: String, updatedOn: Long): CacheResult<Int?>

    fun getTodo(todoId: String): Flow<TodoEntity>

    suspend fun updateTodoDeadline(todoId: String, deadline: Long?, updatedOn: Long): CacheResult<Int?>

    suspend fun updateTodoReminder(todoId: String, reminder: Long?, updatedOn: Long): CacheResult<Int?>

    suspend fun updateTodoCompletion(todoId: String, isComplete: Boolean, completedOn: Long?, updatedOn: Long): CacheResult<Int?>

    suspend fun updateTodoTasksAvailability(todoId: String, tasksAvailability: Boolean, updatedOn: Long): CacheResult<Int?>

    suspend fun updateTodoNotesAvailability(todoId: String, notesAvailability: Boolean, updatedOn: Long): CacheResult<Int?>

    suspend fun updateTodoAttachmentsAvailability(
        todoId: String,
        attachmentsAvailability: Boolean, updatedOn: Long
    ): CacheResult<Int?>

    suspend fun updateTodoAlarmRef(todoId: String, alarmRef: Int?, updatedOn: Long): CacheResult<Int?>

    fun getTodoDetails(todoId: String): Flow<TodoDetailsEntity?>

    fun getTodos(): Flow<List<TodoEntity>>

    suspend fun deleteTodo(todoId: String): CacheResult<Int?>

    suspend fun insertNote(note: NoteEntity): CacheResult<Long?>

    fun getNotes(): Flow<List<NoteEntity>>

    suspend fun deleteNote(noteId: String): CacheResult<Int?>

    suspend fun insertTask(task: TaskEntity): CacheResult<Long?>

    suspend fun updateTaskPosition(taskId: String, position: Int): CacheResult<Int?>

    suspend fun updateTaskTitle(taskId: String, title: String): CacheResult<Int?>

    suspend fun updateTaskCompletion(taskId: String, isComplete: Boolean): CacheResult<Int?>

    suspend fun insertTasks(tasks: List<TaskEntity>): LongArray

    fun getTasks(): Flow<List<TaskEntity>>

    suspend fun deleteTask(taskId: String): CacheResult<Int?>

    suspend fun insertAttachment(attachment: AttachmentEntity): CacheResult<Long?>

    suspend fun insertAttachments(attachments: List<AttachmentEntity>): CacheResult<LongArray?>

    fun getAttachments(): Flow<List<AttachmentEntity>>

    suspend fun deleteAttachment(attachmentId: String): CacheResult<Int?>

    suspend fun insertTodoCategory(todoCategory: TodoCategoryEntity): CacheResult<Long?>

    fun getTodoCategories(): Flow<List<TodoCategoryEntity>>

    suspend fun deleteTodoCategory(todoCategoryName: String): Int

    fun getTodoAndNote(todoId: String): Flow<TodoAndNoteEntity?>

    fun getTodoWithTasks(todoId: String): Flow<TodoWithTasksEntity?>

    fun getTodoWithAttachments(todoId: String): Flow<TodoWithAttachmentsEntity?>

    fun getTodoCategoryWithTodos(todoCategoryName: String): Flow<TodoCategoryWithTodosEntity?>

    fun getTodoCategoriesWithTodos(): Flow<List<TodoCategoryWithTodosEntity>>

    suspend fun saveSelectedTodoId(todoId: String)

    fun getSelectedTodoId(): Flow<String>

    fun getSelectedPieGraphOption(): Flow<Int>

    suspend fun saveSelectedPieGraphOption(selectedOption: Int)
    fun getTask(taskId: String): Flow<TaskEntity?>
    fun getNote(noteId: String): Flow<NoteEntity?>
    fun getAttachment(attachmentId: String): Flow<AttachmentEntity?>
    suspend fun updateTodoUpdatedOn(todoId: String, updatedOn: Long): CacheResult<Int?>
}