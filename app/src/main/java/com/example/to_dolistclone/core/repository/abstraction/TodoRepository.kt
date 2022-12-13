package com.example.to_dolistclone.core.repository.abstraction

import android.net.Uri
import com.example.to_dolistclone.core.data.local.CacheResult
import com.example.to_dolistclone.core.data.remote.ApiResult
import com.example.to_dolistclone.core.data.remote.model.TodoNetwork
import com.example.to_dolistclone.core.domain.model.*
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoCategoryWithTodos
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoWithAttachments
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoWithTasks
import com.example.to_dolistclone.core.domain.model.relation.one_to_one.TodoAndNote
import kotlinx.coroutines.flow.Flow
import java.io.File

interface TodoRepository {

    suspend fun saveShowPrevious(isShow: Boolean)

    suspend fun saveShowToday(isShow: Boolean)

    suspend fun saveShowFuture(isShow: Boolean)

    suspend fun saveShowCompletedToday(isShow: Boolean)

    fun getShowPrevious(): Flow<Boolean>

    fun getShowToday(): Flow<Boolean>

    fun getShowFuture(): Flow<Boolean>

    fun getShowCompletedToday(): Flow<Boolean>

    suspend fun insertTodo(todo: Todo): CacheResult<Long?>

    fun getTodo(todoId: String): Flow<Todo>

    suspend fun updateTodoTitle(todoId: String, title: String, updatedOn: Long): CacheResult<Int?>

    suspend fun updateTodoCategory(todoId: String, category: String, updatedOn: Long): CacheResult<Int?>

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

    fun getTodoDetails(todoId: String): Flow<TodoDetails?>

    fun getTodos(): Flow<List<Todo>>

    suspend fun deleteTodo(todoId: String): CacheResult<Int?>

    suspend fun insertNote(note: Note): CacheResult<Long?>

    suspend fun upsertNoteNetwork(userId: String, note: Note): ApiResult<Unit?>

    fun getNotes(): Flow<List<Note>>

    suspend fun deleteNote(noteId: String): CacheResult<Int?>

    suspend fun insertTask(task: Task): CacheResult<Long?>

    suspend fun updateTaskPosition(taskId: String, position: Int): CacheResult<Int?>

    suspend fun updateTaskTitle(taskId: String, title: String): CacheResult<Int?>

    suspend fun updateTaskCompletion(taskId: String, isComplete: Boolean): CacheResult<Int?>

    suspend fun insertTasks(tasks: List<Task>): LongArray

    fun getTasks(): Flow<List<Task>>

    suspend fun deleteTask(taskId: String): CacheResult<Int?>

    suspend fun insertAttachment(attachment: Attachment): CacheResult<Long?>

    suspend fun insertAttachments(attachments: List<Attachment>): CacheResult<LongArray?>

    fun getAttachments(): Flow<List<Attachment>>

    suspend fun deleteAttachment(attachmentId: String): CacheResult<Int?>

    suspend fun insertTodoCategory(todoCategory: TodoCategory): CacheResult<Long?>

    fun getTodoCategories(): Flow<List<TodoCategory>>

    suspend fun deleteTodoCategory(todoCategoryName: String): Int

    fun getTodoAndNote(todoId: String): Flow<TodoAndNote?>

    fun getTodoWithTasks(todoId: String): Flow<TodoWithTasks?>

    fun getTodoWithAttachments(todoId: String): Flow<TodoWithAttachments?>

    fun getTodoCategoryWithTodos(todoCategoryName: String): Flow<TodoCategoryWithTodos?>

    fun getTodoCategoriesWithTodos(): Flow<List<TodoCategoryWithTodos>>

    suspend fun saveSelectedTodoId(todoId: String)

    fun getSelectedTodoId(): Flow<String>

    fun getSelectedPieGraphOption(): Flow<Int>

    suspend fun saveSelectedPieGraphOption(selectedOption: Int)
    suspend fun upsertTodoCategoryNetwork(
        userId: String,
        todoCategory: TodoCategory
    ): ApiResult<Unit?>

    suspend fun upsertTodoNetwork(userId: String, todo: Todo): ApiResult<Unit?>
    suspend fun upsertTaskNetwork(userId: String, task: Task): ApiResult<Unit?>
    fun getTask(taskId: String): Flow<Task?>
    suspend fun updateTaskNetwork(
        userId: String,
        taskId: String,
        field: Map<String, Any>
    ): ApiResult<Unit?>

    suspend fun deleteTaskNetwork(userId: String, taskId: String): ApiResult<Unit?>
    suspend fun updateTodoNetwork(
        userId: String,
        todoId: String,
        field: Map<String, Any?>
    ): ApiResult<Unit?>

    suspend fun deleteTodoNetwork(userId: String, todoId: String): ApiResult<Unit?>
    suspend fun deleteNoteNetwork(userId: String, noteId: String): ApiResult<Unit?>
    fun getNote(noteId: String): Flow<Note?>
    suspend fun deleteAttachmentNetwork(userId: String, attachmentId: String): ApiResult<Unit?>
    suspend fun upsertAttachmentNetwork(userId: String, attachment: Attachment): ApiResult<Unit?>
    fun getAttachment(attachmentId: String): Flow<Attachment?>
    suspend fun updateTodoUpdatedOn(todoId: String, updatedOn: Long): CacheResult<Int?>
    suspend fun getTodosNetwork(userId: String): ApiResult<List<TodoNetwork>?>
    suspend fun updateTodo(
        todoId: String,
        title: String,
        deadline: Long?,
        reminder: Long?,
        repeat: String?,
        isComplete: Boolean,
        createdOn: Long?,
        updatedOn: Long,
        completedOn: Long?,
        tasksAvailability: Boolean,
        notesAvailability: Boolean,
        attachmentsAvailability: Boolean,
        alarmRef: Int?,
        todoCategoryRefName: String
    ): CacheResult<Int?>

    suspend fun uploadAttachment(userId: String, attachmentPath: String): ApiResult<Unit?>
    suspend fun downloadAttachment(path: String): ApiResult<Unit?>
    suspend fun deleteAttachmentFromFirebaseStorage(path: String): ApiResult<ApiResult<Unit?>?>
    suspend fun uploadAttachment(userId: String, attachmentUri: Uri): Any
}