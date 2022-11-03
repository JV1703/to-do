package com.example.to_dolistclone.core.repository.abstraction

import com.example.to_dolistclone.core.domain.model.*
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoCategoryWithTodos
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoWithAttachments
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoWithTasks
import com.example.to_dolistclone.core.domain.model.relation.one_to_one.TodoAndNote
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun insertTodo(todo: Todo): Long

    fun getTodo(): Flow<Todo>

    suspend fun deleteTodo(todoId: String): Int

    suspend fun insertNote(note: Note): Long

    suspend fun deleteNote(noteId: String): Int

    suspend fun insertTasks(tasks: List<Task>): LongArray

    suspend fun deleteTask(taskId: String): Int

    suspend fun insertAttachment(attachment: Attachment): Long

    suspend fun insertAttachments(attachments: List<Attachment>): LongArray

    suspend fun deleteAttachment(attachmentId: String): Int

    suspend fun insertTodoCategory(todoCategory: TodoCategory): Long

    fun getTodoCategories(): Flow<List<TodoCategory>>

    suspend fun deleteTodoCategory(todoCategoryName: String): Int

    fun getTodoAndNoteWithTodoId(todoId: String): Flow<List<TodoAndNote>>

    fun getTodoWithTasks(todoId: String): Flow<List<TodoWithTasks>>

    fun getTodoWithAttachments(todoId: String): Flow<List<TodoWithAttachments>>

    fun getTodoCategoryWithTodos(todoCategoryName: String): Flow<List<TodoCategoryWithTodos>>

    fun getTodoCategoriesWithTodos(): Flow<List<TodoCategoryWithTodos>>
}