package com.example.to_dolistclone.core.data.local.dao

import androidx.room.*
import com.example.to_dolistclone.core.data.local.model.*
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoCategoryWithTodosEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoWithAttachmentsEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoWithTasksEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_one.TodoAndNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity): Long

    @Query("SELECT * FROM todo")
    fun getTodo(): Flow<TodoEntity>

    @Query("DELETE FROM todo WHERE todoId = :todoId")
    suspend fun deleteTodo(todoId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Query("DELETE FROM note WHERE noteId = :noteId")
    suspend fun deleteNote(noteId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>): LongArray

    @Query("DELETE FROM task WHERE  taskId= :taskId")
    suspend fun deleteTask(taskId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: AttachmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachments(attachments: List<AttachmentEntity>): LongArray

    @Query("DELETE FROM attachment WHERE  attachmentId= :attachmentId")
    suspend fun deleteAttachment(attachmentId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoCategory(todoCategory: TodoCategoryEntity): Long

    @Query("DELETE FROM todo_category WHERE  todoCategoryName= :todoCategoryName")
    suspend fun deleteTodoCategory(todoCategoryName: String): Int

    @Transaction
    @Query("SELECT * FROM todo WHERE todoId = :todoId")
    fun getTodoAndNoteWithTodoId(todoId: String): Flow<List<TodoAndNoteEntity>>

    @Transaction
    @Query("SELECT * FROM todo WHERE todoId = :todoId")
    fun getTodoWithTasks(todoId: String): Flow<List<TodoWithTasksEntity>>

    @Transaction
    @Query("SELECT * FROM todo WHERE todoId = :todoId")
    fun getTodoWithAttachments(todoId: String): Flow<List<TodoWithAttachmentsEntity>>

    @Transaction
    @Query("SELECT * FROM todo_category WHERE todoCategoryName = :todoCategoryName")
    fun getTodoCategoryWithTodos(todoCategoryName: String): Flow<List<TodoCategoryWithTodosEntity>>

    @Transaction
    @Query("SELECT * FROM todo_category")
    fun getTodoCategoriesWithTodos(): Flow<List<TodoCategoryWithTodosEntity>>

}