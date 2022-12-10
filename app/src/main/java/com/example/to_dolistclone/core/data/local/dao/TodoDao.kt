package com.example.to_dolistclone.core.data.local.dao

import androidx.room.*
import com.example.to_dolistclone.core.data.local.model.*
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoCategoryWithTodosEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoDetailsEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoWithAttachmentsEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoWithTasksEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_one.TodoAndNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodo(todo: TodoEntity): Long

    @Query("UPDATE todo SET title = :title WHERE todoId = :todoId")
    suspend fun updateTodoTitle(todoId: String, title: String): Int

    @Query("UPDATE todo SET todoCategoryRefName = :category WHERE todoId = :todoId")
    suspend fun updateTodoCategory(todoId: String, category: String): Int

    @Query("SELECT * FROM todo WHERE todoId = :todoId")
    fun getTodo(todoId: String): Flow<TodoEntity>

    @Query("SELECT * FROM todo")
    fun getTodos(): Flow<List<TodoEntity>>

    @Query("DELETE FROM todo WHERE todoId = :todoId")
    suspend fun deleteTodo(todoId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Query("SELECT * FROM note WHERE noteId = :noteId")
    fun getNote(noteId: String): Flow<NoteEntity?>

    @Query("SELECT * FROM note")
    fun getNotes(): Flow<List<NoteEntity>>

    @Query("UPDATE todo SET deadline = :deadline WHERE todoId = :todoId")
    suspend fun updateTodoDeadline(todoId: String, deadline: Long?): Int

    @Query("UPDATE todo SET reminder = :reminder WHERE todoId = :todoId")
    suspend fun updateTodoReminder(todoId: String, reminder: Long?): Int

    @Query("UPDATE todo SET isComplete = :isComplete, completedOn = :completedOn WHERE todoId = :todoId")
    suspend fun updateTodoCompletion(todoId: String, isComplete: Boolean, completedOn: Long?): Int

    @Query("UPDATE todo SET tasks = :tasksAvailability WHERE todoId = :todoId")
    suspend fun updateTodoTasksAvailability(todoId: String, tasksAvailability: Boolean): Int

    @Query("UPDATE todo SET notes = :notesAvailability WHERE todoId = :todoId")
    suspend fun updateTodoNotesAvailability(todoId: String, notesAvailability: Boolean): Int

    @Query("UPDATE todo SET attachments = :attachmentsAvailability WHERE todoId = :todoId")
    suspend fun updateTodoAttachmentsAvailability(
        todoId: String, attachmentsAvailability: Boolean
    ): Int

    @Query("UPDATE todo SET alarmRef = :alarmRef WHERE todoId = :todoId")
    suspend fun updateTodoAlarmRef(todoId: String, alarmRef: Int?): Int

    @Query("DELETE FROM note WHERE noteId = :noteId")
    suspend fun deleteNote(noteId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: TaskEntity): Long

    @Query("UPDATE task SET position = :position WHERE taskId = :taskId")
    suspend fun updateTaskPosition(taskId: String, position: Int): Int

    @Query("UPDATE task SET task = :title WHERE taskId = :taskId")
    suspend fun updateTaskTitle(taskId: String, title: String): Int

    @Query("UPDATE task SET isComplete = :isComplete WHERE taskId = :taskId")
    suspend fun updateTaskCompletion(taskId: String, isComplete: Boolean): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTasks(tasks: List<TaskEntity>): LongArray

    @Query("SELECT * FROM task WHERE taskId= :taskId ")
    fun getTask(taskId: String): Flow<TaskEntity?>

    @Query("SELECT * FROM task")
    fun getTasks(): Flow<List<TaskEntity>>

    @Query("DELETE FROM task WHERE  taskId= :taskId")
    suspend fun deleteTask(taskId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAttachment(attachment: AttachmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAttachments(attachments: List<AttachmentEntity>): LongArray

    @Query("SELECT * FROM attachment WHERE attachmentId = :attachmentId")
    fun getAttachment(attachmentId: String): Flow<AttachmentEntity?>

    @Query("SELECT * FROM attachment")
    fun getAttachments(): Flow<List<AttachmentEntity>>

    @Query("DELETE FROM attachment WHERE  attachmentId= :attachmentId")
    suspend fun deleteAttachment(attachmentId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodoCategory(todoCategory: TodoCategoryEntity): Long

    @Query("SELECT * FROM todo_category")
    fun getTodoCategories(): Flow<List<TodoCategoryEntity>>

    @Query("DELETE FROM todo_category WHERE  todoCategoryName= :todoCategoryName")
    suspend fun deleteTodoCategory(todoCategoryName: String): Int

    @Transaction
    @Query("SELECT * FROM todo WHERE todoId = :todoId")
    fun getTodoAndNote(todoId: String): Flow<TodoAndNoteEntity?>

    @Transaction
    @Query("SELECT * FROM todo WHERE todoId = :todoId")
    fun getTodoWithTasks(todoId: String): Flow<TodoWithTasksEntity?>

    @Transaction
    @Query("SELECT * FROM todo WHERE todoId = :todoId")
    fun getTodoWithAttachments(todoId: String): Flow<TodoWithAttachmentsEntity?>

    @Transaction
    @Query("SELECT * FROM todo_category WHERE todoCategoryName = :todoCategoryName")
    fun getTodoCategoryWithTodos(todoCategoryName: String): Flow<TodoCategoryWithTodosEntity?>

    @Transaction
    @Query("SELECT * FROM todo_category")
    fun getTodoCategoriesWithTodos(): Flow<List<TodoCategoryWithTodosEntity>>

    @Transaction
    @Query("SELECT * FROM todo WHERE todoId = :todoId")
    fun getTodoDetails(todoId: String): Flow<TodoDetailsEntity?>

}