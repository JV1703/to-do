package com.example.to_dolistclone.core.data.local.abstraction

import com.example.to_dolistclone.core.data.local.model.*
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoCategoryWithTodosEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoWithAttachmentsEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoWithTasksEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_one.TodoAndNoteEntity
import com.example.to_dolistclone.core.domain.model.TodoCategory
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun insertTodo(todo: TodoEntity): Long

    fun getTodo(): Flow<TodoEntity>

    suspend fun deleteTodo(todoId: String): Int

    suspend fun insertNote(note: NoteEntity): Long

    suspend fun deleteNote(noteId: String): Int

    suspend fun insertTasks(tasks: List<TaskEntity>): LongArray

    suspend fun deleteTask(taskId: String): Int

    suspend fun insertAttachment(attachment: AttachmentEntity): Long

    suspend fun deleteAttachment(attachmentId: String): Int

    suspend fun insertAttachments(attachments: List<AttachmentEntity>): LongArray

    suspend fun insertTodoCategory(todoCategory: TodoCategoryEntity): Long

    fun getTodoCategories(): Flow<List<TodoCategoryEntity>>

    suspend fun deleteTodoCategory(todoCategoryName: String): Int

    fun getTodoAndNoteWithTodoId(todoId: String): Flow<List<TodoAndNoteEntity>>

    fun getTodoWithTasks(todoId: String): Flow<List<TodoWithTasksEntity>>

    fun getTodoWithAttachments(todoId: String): Flow<List<TodoWithAttachmentsEntity>>

    fun getTodoCategoryWithTodos(todoCategoryName: String): Flow<List<TodoCategoryWithTodosEntity>>

    fun getTodoCategoriesWithTodos(): Flow<List<TodoCategoryWithTodosEntity>>
}