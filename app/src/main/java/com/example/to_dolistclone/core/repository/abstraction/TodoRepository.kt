package com.example.to_dolistclone.core.repository.abstraction

import com.example.to_dolistclone.core.data.local.model.TodoEntity
import com.example.to_dolistclone.core.domain.model.*
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoCategoryWithTodos
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoWithAttachments
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoWithTasks
import com.example.to_dolistclone.core.domain.model.relation.one_to_one.TodoAndNote
import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    suspend fun saveShowPrevious(isShow: Boolean)

    suspend fun saveShowToday(isShow: Boolean)

    suspend fun saveShowFuture(isShow: Boolean)

    suspend fun saveShowCompletedToday(isShow: Boolean)

    fun getShowPrevious(): Flow<Boolean>

    fun getShowToday(): Flow<Boolean>

    fun getShowFuture(): Flow<Boolean>

    fun getShowCompletedToday(): Flow<Boolean>

    suspend fun insertTodo(todo: Todo): Long

    fun getTodo(todoId: String): Flow<Todo>

    suspend fun updateTodoTitle(todoId: String, title: String): Int

    suspend fun updateTodoCategory(todoId: String, category: String): Int

    suspend fun updateTodoDeadline(todoId: String, deadline: Long?): Int

    suspend fun updateTodoReminder(todoId: String, reminder: Long?): Int

    suspend fun updateTodoCompletion(todoId: String, isComplete: Boolean, completedOn: Long?): Int

    suspend fun updateTodoTasksAvailability(todoId: String, tasksAvailability: Boolean): Int

    suspend fun updateTodoNotesAvailability(todoId: String, notesAvailability: Boolean): Int

    suspend fun updateTodoAttachmentsAvailability(todoId: String, attachmentsAvailability: Boolean): Int

    fun getTodoDetails(todoId: String): Flow<TodoDetails?>

    fun getTodos(): Flow<List<Todo>>

    fun getTodos(from: Long, to: Long): Flow<List<Todo>>

    suspend fun deleteTodo(todoId: String): Int

    suspend fun insertNote(note: Note): Long

    suspend fun deleteNote(noteId: String): Int

    suspend fun insertTask(task: Task): Long

    suspend fun getTaskSize(): Int

    suspend fun updateTaskPosition(taskId: String, position: Int): Int

    suspend fun updateTaskTitle(taskId: String, title: String): Int

    suspend fun updateTaskCompletion(taskId: String, isComplete: Boolean): Int

    suspend fun insertTasks(tasks: List<Task>): LongArray

    suspend fun deleteTask(taskId: String): Int

    suspend fun insertAttachment(attachment: Attachment): Long

    suspend fun insertAttachments(attachments: List<Attachment>): LongArray

    suspend fun deleteAttachment(attachmentId: String): Int

    suspend fun insertTodoCategory(todoCategory: TodoCategory): Long

    fun getTodoCategories(): Flow<List<TodoCategory>>

    suspend fun deleteTodoCategory(todoCategoryName: String): Int

    fun getTodoAndNoteWithTodoId(todoId: String): Flow<TodoAndNote>

    fun getTodoWithTasks(todoId: String): Flow<TodoWithTasks>

    fun getTodoWithAttachments(todoId: String): Flow<TodoWithAttachments>

    fun getTodoCategoryWithTodos(todoCategoryName: String): Flow<List<TodoCategoryWithTodos>>

    fun getTodoCategoriesWithTodos(): Flow<List<TodoCategoryWithTodos>>

    suspend fun saveSelectedNoteId(todoId: String)

    fun getSelectedNoteId(): Flow<String>
}