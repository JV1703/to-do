package com.example.to_dolistclone.core.repository.implementation

import android.util.Log
import com.example.to_dolistclone.core.data.local.abstraction.LocalDataSource
import com.example.to_dolistclone.core.data.local.model.TodoEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.toTodoCategoryWithTodos
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.toTodoDetails
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.toTodoWithAttachments
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.toTodoWithTasks
import com.example.to_dolistclone.core.data.local.model.relations.one_to_one.toTodoAndNote
import com.example.to_dolistclone.core.data.local.model.toTodo
import com.example.to_dolistclone.core.data.local.model.toTodoCategory
import com.example.to_dolistclone.core.domain.model.*
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoCategoryWithTodos
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoWithAttachments
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoWithTasks
import com.example.to_dolistclone.core.domain.model.relation.one_to_one.TodoAndNote
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(private val local: LocalDataSource) : TodoRepository {

    override suspend fun saveShowPrevious(isShow: Boolean) {
        local.saveShowPrevious(isShow)
    }

    override suspend fun saveShowToday(isShow: Boolean) {
        local.saveShowToday(isShow)
    }

    override suspend fun saveShowFuture(isShow: Boolean) {
        local.saveShowFuture(isShow)
    }

    override suspend fun saveShowCompletedToday(isShow: Boolean) {
        local.saveShowCompletedToday(isShow)
    }

    override fun getShowPrevious(): Flow<Boolean> = local.getShowPrevious()

    override fun getShowToday(): Flow<Boolean> = local.getShowToday()

    override fun getShowFuture(): Flow<Boolean> = local.getShowFuture()

    override fun getShowCompletedToday(): Flow<Boolean> = local.getShowCompletedToday()

    override suspend fun insertTodo(todo: Todo): Long = local.insertTodo(todo.toTodoEntity())

    override suspend fun updateTodoTitle(todoId: String, title: String): Int =
        local.updateTodoTitle(todoId, title)

    override suspend fun updateTodoCategory(todoId: String, category: String): Int =
        local.updateTodoCategory(todoId, category)

    override suspend fun updateTodoDeadline(todoId: String, deadline: Long?): Int =
        local.updateTodoDeadline(todoId, deadline)

    override suspend fun updateTodoReminder(todoId: String, reminder: Long?): Int =
        local.updateTodoReminder(todoId, reminder)

    override suspend fun updateTodoCompletion(
        todoId: String, isComplete: Boolean, completedOn: Long?
    ): Int = local.updateTodoCompletion(todoId, isComplete, completedOn)

    override suspend fun updateTodoTasksAvailability(
        todoId: String, tasksAvailability: Boolean
    ): Int = local.updateTodoTasksAvailability(todoId, tasksAvailability)

    override suspend fun updateTodoNotesAvailability(
        todoId: String, notesAvailability: Boolean
    ): Int = updateTodoNotesAvailability(todoId, notesAvailability)

    override suspend fun updateTodoAttachmentsAvailability(
        todoId: String, attachmentsAvailability: Boolean
    ): Int = updateTodoAttachmentsAvailability(todoId, attachmentsAvailability)

    override fun getTodo(todoId: String): Flow<Todo> = local.getTodo(todoId).map { it.toTodo() }

    override fun getTodos(): Flow<List<Todo>> = local.getTodos().map { listEntityModel ->
        listEntityModel.map { entityModel ->
            entityModel.toTodo()
        }
    }.catch { e ->
        Log.e("todoRepository", "getTodos errorMsg: ${e.message}")
    }

    override fun getTodos(from: Long, to: Long): Flow<List<Todo>> = local.getTodos().map { listEntityModel ->
        listEntityModel.map { entityModel ->
            entityModel.toTodo()
        }
    }

    override fun getTodoDetails(todoId: String): Flow<TodoDetails?> =
        local.getTodoDetails(todoId).map { it?.let { it.toTodoDetails() } }.catch { e ->
            Log.e("todoRepository", "getTodoDetails errorMsg: ${e.message}")
        }

    override suspend fun deleteTodo(todoId: String): Int = local.deleteTodo(todoId)

    override suspend fun insertNote(note: Note): Long = local.insertNote(note.toNoteEntity())

    override suspend fun deleteNote(noteId: String): Int = local.deleteNote(noteId)

    override suspend fun insertTask(task: Task): Long = local.insertTask(task.toTaskEntity())

    override suspend fun getTaskSize(): Int = local.getTaskSize()

    override suspend fun updateTaskPosition(taskId: String, position: Int): Int =
        local.updateTaskPosition(taskId, position)

    override suspend fun updateTaskTitle(taskId: String, title: String): Int =
        local.updateTaskTitle(taskId, title)

    override suspend fun updateTaskCompletion(taskId: String, isComplete: Boolean): Int =
        local.updateTaskCompletion(taskId, isComplete)

    override suspend fun insertTasks(tasks: List<Task>): LongArray =
        local.insertTasks(tasks.map { it.toTaskEntity() })

    override suspend fun deleteTask(taskId: String): Int = local.deleteTask(taskId)

    override suspend fun insertAttachment(attachment: Attachment): Long =
        local.insertAttachment(attachment.toAttachmentEntity())

    override suspend fun insertAttachments(attachments: List<Attachment>): LongArray =
        local.insertAttachments(attachments.map { it.toAttachmentEntity() })

    override suspend fun deleteAttachment(attachmentId: String): Int =
        local.deleteAttachment(attachmentId)

    override suspend fun insertTodoCategory(todoCategory: TodoCategory): Long =
        local.insertTodoCategory(todoCategory.toTodoEntity())

    override fun getTodoCategories(): Flow<List<TodoCategory>> =
        local.getTodoCategories().map { listEntityModel ->
            listEntityModel.map { entityModel ->
                entityModel.toTodoCategory()
            }
        }

    override suspend fun deleteTodoCategory(todoCategoryName: String): Int =
        local.deleteTodoCategory(todoCategoryName)

    override fun getTodoAndNoteWithTodoId(todoId: String): Flow<TodoAndNote> =
        local.getTodoAndNoteWithTodoId(todoId).map { entityModel ->
            entityModel.toTodoAndNote()
        }

    override fun getTodoWithTasks(todoId: String): Flow<TodoWithTasks> =
        local.getTodoWithTasks(todoId).map { entityModel ->
            entityModel.toTodoWithTasks()
        }

    override fun getTodoWithAttachments(todoId: String): Flow<TodoWithAttachments> =
        local.getTodoWithAttachments(todoId).map { entityModel ->
            entityModel.toTodoWithAttachments()
        }

    override fun getTodoCategoryWithTodos(todoCategoryName: String): Flow<List<TodoCategoryWithTodos>> =
        local.getTodoCategoryWithTodos(todoCategoryName).map { listEntityModel ->
            listEntityModel.map { entityModel ->
                entityModel.toTodoCategoryWithTodos()
            }
        }

    override fun getTodoCategoriesWithTodos(): Flow<List<TodoCategoryWithTodos>> =
        local.getTodoCategoriesWithTodos().map { listEntityModel ->
            listEntityModel.map { entityModel ->
                entityModel.toTodoCategoryWithTodos()
            }
        }

    override suspend fun saveSelectedNoteId(todoId: String) {
        local.saveSelectedNoteId(todoId)
    }

    override fun getSelectedNoteId(): Flow<String> =
        local.getSelectedNoteId()
}
