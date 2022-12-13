package com.example.to_dolistclone.core.repository.implementation

import android.net.Uri
import android.util.Log
import com.example.to_dolistclone.core.data.local.CacheResult
import com.example.to_dolistclone.core.data.local.abstraction.LocalDataSource
import com.example.to_dolistclone.core.data.local.model.*
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.toTodoCategoryWithTodos
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.toTodoDetails
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.toTodoWithAttachments
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.toTodoWithTasks
import com.example.to_dolistclone.core.data.local.model.relations.one_to_one.toTodoAndNote
import com.example.to_dolistclone.core.data.remote.ApiResult
import com.example.to_dolistclone.core.data.remote.abstraction.RemoteDataSource
import com.example.to_dolistclone.core.data.remote.model.TodoNetwork
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

class TodoRepositoryImpl @Inject constructor(
    private val local: LocalDataSource, private val remote: RemoteDataSource
) : TodoRepository {

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

    override suspend fun insertTodo(todo: Todo): CacheResult<Long?> =
        local.insertTodo(todo.toTodoEntity())

    override suspend fun upsertTodoNetwork(userId: String, todo: Todo) =
        remote.upsertTodo(userId, todo.toTodoNetwork())

    override suspend fun updateTodo(
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
    ) = local.updateTodo(
        todoId,
        title,
        deadline,
        reminder,
        repeat,
        isComplete,
        createdOn,
        updatedOn,
        completedOn,
        tasksAvailability,
        notesAvailability,
        attachmentsAvailability,
        alarmRef,
        todoCategoryRefName
    )

    override suspend fun updateTodoUpdatedOn(todoId: String, updatedOn: Long) =
        local.updateTodoUpdatedOn(
            todoId = todoId, updatedOn = updatedOn
        )

    override suspend fun updateTodoNetwork(
        userId: String, todoId: String, field: Map<String, Any?>
    ) = remote.updateTodo(userId, todoId, field)

    override suspend fun updateTodoTitle(
        todoId: String, title: String, updatedOn: Long
    ): CacheResult<Int?> = local.updateTodoTitle(todoId, title, updatedOn)

    override suspend fun updateTodoCategory(
        todoId: String, category: String, updatedOn: Long
    ): CacheResult<Int?> = local.updateTodoCategory(todoId, category, updatedOn)

    override suspend fun updateTodoDeadline(
        todoId: String, deadline: Long?, updatedOn: Long
    ): CacheResult<Int?> = local.updateTodoDeadline(todoId, deadline, updatedOn)

    override suspend fun updateTodoReminder(
        todoId: String, reminder: Long?, updatedOn: Long
    ): CacheResult<Int?> = local.updateTodoReminder(todoId, reminder, updatedOn)

    override suspend fun updateTodoCompletion(
        todoId: String, isComplete: Boolean, completedOn: Long?, updatedOn: Long
    ): CacheResult<Int?> = local.updateTodoCompletion(todoId, isComplete, completedOn, updatedOn)

    override suspend fun updateTodoTasksAvailability(
        todoId: String, tasksAvailability: Boolean, updatedOn: Long
    ): CacheResult<Int?> = local.updateTodoTasksAvailability(todoId, tasksAvailability, updatedOn)

    override suspend fun updateTodoNotesAvailability(
        todoId: String, notesAvailability: Boolean, updatedOn: Long
    ): CacheResult<Int?> = local.updateTodoNotesAvailability(todoId, notesAvailability, updatedOn)

    override suspend fun updateTodoAttachmentsAvailability(
        todoId: String, attachmentsAvailability: Boolean, updatedOn: Long
    ): CacheResult<Int?> =
        local.updateTodoAttachmentsAvailability(todoId, attachmentsAvailability, updatedOn)

    override suspend fun updateTodoAlarmRef(
        todoId: String, alarmRef: Int?, updatedOn: Long
    ): CacheResult<Int?> = local.updateTodoAlarmRef(todoId, alarmRef, updatedOn)

    override fun getTodo(todoId: String): Flow<Todo> = local.getTodo(todoId).map { it.toTodo() }

    override fun getTodos(): Flow<List<Todo>> = local.getTodos().map { listEntityModel ->
        listEntityModel.map { entityModel ->
            entityModel.toTodo()
        }
    }.catch { e ->
        Log.e("todoRepository", "getTodos errorMsg: ${e.message}")
    }

    override suspend fun getTodosNetwork(userId: String): ApiResult<List<TodoNetwork>?> =
        remote.getTodos(userId)

    override fun getTodoDetails(todoId: String): Flow<TodoDetails?> =
        local.getTodoDetails(todoId).map { it?.toTodoDetails() }.catch { e ->
            Log.e("todoRepository", "getTodoDetails errorMsg: ${e.message}")
        }

    override suspend fun deleteTodo(todoId: String): CacheResult<Int?> = local.deleteTodo(todoId)

    override suspend fun deleteTodoNetwork(userId: String, todoId: String) = remote.deleteTodo(
        userId = userId, todoId = todoId
    )

    override suspend fun insertNote(note: Note): CacheResult<Long?> =
        local.insertNote(note.toNoteEntity())

    override suspend fun deleteNoteNetwork(userId: String, noteId: String) = remote.deleteNote(
        userId = userId, noteId = noteId
    )

    override suspend fun upsertNoteNetwork(userId: String, note: Note): ApiResult<Unit?> =
        remote.upsertNote(userId, note.toNoteNetwork())

    override fun getNote(noteId: String): Flow<Note?> =
        local.getNote(noteId).map { entityModel -> entityModel?.toNote() }

    override fun getNotes(): Flow<List<Note>> = local.getNotes()
        .map { listEntityModel -> listEntityModel.map { entityModel -> entityModel.toNote() } }

    override suspend fun deleteNote(noteId: String): CacheResult<Int?> = local.deleteNote(noteId)

    override suspend fun insertTask(task: Task): CacheResult<Long?> =
        local.insertTask(task.toTaskEntity())

    override suspend fun upsertTaskNetwork(userId: String, task: Task) =
        remote.insertTask(userId, task.toTaskNetwork())

    override suspend fun updateTaskPosition(taskId: String, position: Int): CacheResult<Int?> =
        local.updateTaskPosition(taskId, position)

    override suspend fun updateTaskTitle(taskId: String, title: String): CacheResult<Int?> =
        local.updateTaskTitle(taskId, title)

    override suspend fun updateTaskNetwork(
        userId: String, taskId: String, field: Map<String, Any>
    ) = remote.updateTask(userId, taskId, field)

    override suspend fun updateTaskCompletion(
        taskId: String, isComplete: Boolean
    ): CacheResult<Int?> = local.updateTaskCompletion(taskId, isComplete)

    override suspend fun insertTasks(tasks: List<Task>): LongArray =
        local.insertTasks(tasks.map { it.toTaskEntity() })

    override fun getTask(taskId: String): Flow<Task?> = local.getTask(taskId).map { it?.toTask() }

    override fun getTasks(): Flow<List<Task>> = local.getTasks().map { listEntityModel ->
        listEntityModel.map { entityModel ->
            entityModel.toTask()
        }
    }

    override suspend fun deleteTask(taskId: String): CacheResult<Int?> = local.deleteTask(taskId)

    override suspend fun deleteTaskNetwork(userId: String, taskId: String) =
        remote.deleteTask(userId, taskId)

    override suspend fun insertAttachment(attachment: Attachment): CacheResult<Long?> =
        local.insertAttachment(attachment.toAttachmentEntity())

    override suspend fun upsertAttachmentNetwork(userId: String, attachment: Attachment): ApiResult<Unit?> {
        Log.i("TodoRepository", "attachment: $attachment")
        return remote.insertAttachment(
            userId = userId, attachment = attachment.toAttachmentNetwork()
        )
    }

    override suspend fun uploadAttachment(userId: String, attachmentUri: Uri): ApiResult<Unit?> = remote.uploadAttachment(
        userId = userId,
        attachmentUri = attachmentUri
    )

    override suspend fun insertAttachments(attachments: List<Attachment>): CacheResult<LongArray?> =
        local.insertAttachments(attachments.map { it.toAttachmentEntity() })

    override fun getAttachment(attachmentId: String): Flow<Attachment?> =
        local.getAttachment(attachmentId).map { it?.toAttachment() }

    override fun getAttachments(): Flow<List<Attachment>> =
        local.getAttachments().map { listEntityModel ->
            listEntityModel.map { entityModel ->
                entityModel.toAttachment()
            }
        }

    override suspend fun downloadAttachment(path: String): ApiResult<Unit?> =
        remote.downloadAttachment(path)

    override suspend fun deleteAttachment(attachmentId: String): CacheResult<Int?> =
        local.deleteAttachment(attachmentId)

    override suspend fun deleteAttachmentNetwork(userId: String, attachmentId: String) =
        remote.deleteAttachment(
            userId = userId, attachmentId = attachmentId
        )

    override suspend fun deleteAttachmentFromFirebaseStorage(path: String) =
        remote.deleteAttachmentFromFirebaseStorage(path)

    override suspend fun insertTodoCategory(todoCategory: TodoCategory): CacheResult<Long?> =
        local.insertTodoCategory(todoCategory.toTodoCategoryEntity())

    override suspend fun upsertTodoCategoryNetwork(userId: String, todoCategory: TodoCategory) =
        remote.upsertTodoCategory(
            userId = userId, todoCategory = todoCategory.toTodoCategoryNetwork()
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
