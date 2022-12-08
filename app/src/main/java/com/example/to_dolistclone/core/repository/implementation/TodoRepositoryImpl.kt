package com.example.to_dolistclone.core.repository.implementation

import android.util.Log
import com.example.to_dolistclone.core.data.local.CacheResult
import com.example.to_dolistclone.core.data.local.abstraction.LocalDataSource
import com.example.to_dolistclone.core.data.local.model.*
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.toTodoCategoryWithTodos
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.toTodoDetails
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.toTodoWithAttachments
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.toTodoWithTasks
import com.example.to_dolistclone.core.data.local.model.relations.one_to_one.toTodoAndNote
import com.example.to_dolistclone.core.data.remote.abstraction.RemoteDataSource
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

class TodoRepositoryImpl @Inject constructor(private val local: LocalDataSource, private val remote: RemoteDataSource) : TodoRepository {

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
    ): Int = local.updateTodoNotesAvailability(todoId, notesAvailability)

    override suspend fun updateTodoAttachmentsAvailability(
        todoId: String, attachmentsAvailability: Boolean
    ): Int = local.updateTodoAttachmentsAvailability(todoId, attachmentsAvailability)

    override suspend fun updateTodoAlarmRef(todoId: String, alarmRef: Int?): Int =
        local.updateTodoAlarmRef(todoId, alarmRef)

    override fun getTodo(todoId: String): Flow<Todo> = local.getTodo(todoId).map { it.toTodo() }

    override fun getTodos(): Flow<List<Todo>> = local.getTodos().map { listEntityModel ->
        listEntityModel.map { entityModel ->
            entityModel.toTodo()
        }
    }.catch { e ->
        Log.e("todoRepository", "getTodos errorMsg: ${e.message}")
    }

    override fun getTodoDetails(todoId: String): Flow<TodoDetails?> =
        local.getTodoDetails(todoId).map { it?.toTodoDetails() }.catch { e ->
            Log.e("todoRepository", "getTodoDetails errorMsg: ${e.message}")
        }

    override suspend fun deleteTodo(todoId: String): Int = local.deleteTodo(todoId)

    override suspend fun insertNote(note: Note): Long = local.insertNote(note.toNoteEntity())

    override fun getNotes(): Flow<List<Note>> = local.getNotes()
        .map { listEntityModel -> listEntityModel.map { entityModel -> entityModel.toNote() } }

    override suspend fun deleteNote(noteId: String): Int = local.deleteNote(noteId)

    override suspend fun insertTask(task: Task): Long = local.insertTask(task.toTaskEntity())

    override suspend fun updateTaskPosition(taskId: String, position: Int): Int =
        local.updateTaskPosition(taskId, position)

    override suspend fun updateTaskTitle(taskId: String, title: String): Int =
        local.updateTaskTitle(taskId, title)

    override suspend fun updateTaskCompletion(taskId: String, isComplete: Boolean): Int =
        local.updateTaskCompletion(taskId, isComplete)

    override suspend fun insertTasks(tasks: List<Task>): LongArray =
        local.insertTasks(tasks.map { it.toTaskEntity() })

    override fun getTasks(): Flow<List<Task>> = local.getTasks().map { listEntityModel ->
        listEntityModel.map { entityModel ->
            entityModel.toTask()
        }
    }

    override suspend fun deleteTask(taskId: String): Int = local.deleteTask(taskId)

    override suspend fun insertAttachment(attachment: Attachment): Long =
        local.insertAttachment(attachment.toAttachmentEntity())

    override suspend fun insertAttachments(attachments: List<Attachment>): LongArray =
        local.insertAttachments(attachments.map { it.toAttachmentEntity() })

    override fun getAttachments(): Flow<List<Attachment>> =
        local.getAttachments().map { listEntityModel ->
            listEntityModel.map { entityModel ->
                entityModel.toAttachment()
            }
        }

    override suspend fun deleteAttachment(attachmentId: String): Int =
        local.deleteAttachment(attachmentId)

    override suspend fun insertTodoCategory(todoCategory: TodoCategory): CacheResult<Long?> =
        local.insertTodoCategory(todoCategory.toTodoCategoryEntity())

    override suspend fun upsertTodoCategoryNetwork(userId: String, todoCategory: TodoCategory) = remote.upsertTodoCategory(
        userId = userId,
        todoCategory = todoCategory.toTodoCategoryNetwork()
    )

    override fun getTodoCategories(): Flow<List<TodoCategory>> =
        local.getTodoCategories().map { listEntityModel ->
            listEntityModel.map { entityModel ->
                entityModel.toTodoCategory()
            }
        }

    override suspend fun deleteTodoCategory(todoCategoryName: String): Int =
        local.deleteTodoCategory(todoCategoryName)

    override fun getTodoAndNote(todoId: String): Flow<TodoAndNote?> =
        local.getTodoAndNote(todoId).map { entityModel ->
            entityModel?.toTodoAndNote()
        }

    override fun getTodoWithTasks(todoId: String): Flow<TodoWithTasks?> =
        local.getTodoWithTasks(todoId).map { entityModel ->
            entityModel?.toTodoWithTasks()
        }

    override fun getTodoWithAttachments(todoId: String): Flow<TodoWithAttachments?> =
        local.getTodoWithAttachments(todoId).map { entityModel ->
            entityModel?.toTodoWithAttachments()
        }

    override fun getTodoCategoryWithTodos(todoCategoryName: String): Flow<TodoCategoryWithTodos?> =
        local.getTodoCategoryWithTodos(todoCategoryName).map { entityModel ->
            entityModel?.toTodoCategoryWithTodos()
        }

    override fun getTodoCategoriesWithTodos(): Flow<List<TodoCategoryWithTodos>> =
        local.getTodoCategoriesWithTodos().map { listEntityModel ->
            listEntityModel.map { entityModel ->
                entityModel.toTodoCategoryWithTodos()
            }
        }

    override suspend fun saveSelectedTodoId(todoId: String) {
        local.saveSelectedTodoId(todoId)
    }

    override fun getSelectedTodoId(): Flow<String> = local.getSelectedTodoId()

    override fun getSelectedPieGraphOption(): Flow<Int> = local.getSelectedPieGraphOption()

    override suspend fun saveSelectedPieGraphOption(selectedOption: Int) {
        local.saveSelectedPieGraphOption(selectedOption)
    }
}
