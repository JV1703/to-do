package com.example.to_dolistclone.core.data.local.implementation

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.to_dolistclone.core.data.local.abstraction.LocalDataSource
import com.example.to_dolistclone.core.data.local.dao.TodoDao
import com.example.to_dolistclone.core.data.local.implementation.PreferencesKeys.SELECTED_TODO_ID
import com.example.to_dolistclone.core.data.local.implementation.PreferencesKeys.SHOW_COMPLETED_TODAY
import com.example.to_dolistclone.core.data.local.implementation.PreferencesKeys.SHOW_FUTURE
import com.example.to_dolistclone.core.data.local.implementation.PreferencesKeys.SHOW_PREVIOUS
import com.example.to_dolistclone.core.data.local.implementation.PreferencesKeys.SHOW_TODAY
import com.example.to_dolistclone.core.data.local.model.*
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoCategoryWithTodosEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoDetailsEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoWithAttachmentsEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoWithTasksEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_one.TodoAndNoteEntity
import com.example.to_dolistclone.core.di.coroutine_dispatchers.CoroutinesQualifiers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

object PreferencesKeys {
    val SHOW_PREVIOUS = booleanPreferencesKey("show_previous")
    val SHOW_TODAY = booleanPreferencesKey("show_today")
    val SHOW_FUTURE = booleanPreferencesKey("show_future")
    val SHOW_COMPLETED_TODAY = booleanPreferencesKey("show_completed_today")
    val SELECTED_TODO_ID = stringPreferencesKey("selected_todo_id")
}

class LocalDataSourceImpl @Inject constructor(
    private val todoDao: TodoDao,
    private val dataStore: DataStore<Preferences>,
    @CoroutinesQualifiers.IoDispatcher private val dispatcherIO: CoroutineDispatcher
) : LocalDataSource {

    override suspend fun saveSelectedNoteId(todoId: String) {
        dataStore.edit { preferences ->
            preferences[SELECTED_TODO_ID] = todoId
        }
    }

    override suspend fun saveShowPrevious(isShow: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_PREVIOUS] = isShow
        }
    }

    override suspend fun saveShowToday(isShow: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_TODAY] = isShow
        }
    }

    override suspend fun saveShowFuture(isShow: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_FUTURE] = isShow
        }
    }

    override suspend fun saveShowCompletedToday(isShow: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_COMPLETED_TODAY] = isShow
        }
    }

    override fun getSelectedNoteId(): Flow<String> = dataStore.data.map { preferences ->
        preferences[SELECTED_TODO_ID] ?: ""
    }.catch { e ->
        Log.e("LocalDataSourceImpl", "getShowToday errorMsg: ${e.message}")
    }

    override fun getShowPrevious(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SHOW_PREVIOUS] ?: true
    }.catch { e ->
        Log.e("LocalDataSourceImpl", "getShowToday errorMsg: ${e.message}")
    }

    override fun getShowToday(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SHOW_TODAY] ?: true
    }.catch { e ->
        Log.e("LocalDataSourceImpl", "getShowToday errorMsg: ${e.message}")
    }

    override fun getShowFuture(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SHOW_FUTURE] ?: true
    }.catch { e ->
        Log.e("LocalDataSourceImpl", "getShowToday errorMsg: ${e.message}")
    }

    override fun getShowCompletedToday(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SHOW_COMPLETED_TODAY] ?: true
    }.catch { e ->
        Log.e("LocalDataSourceImpl", "getShowToday errorMsg: ${e.message}")
    }

    override suspend fun insertTodo(todo: TodoEntity): Long {
        return withContext(dispatcherIO) { todoDao.insertTodo(todo) }
    }

    override suspend fun updateTodoTitle(todoId: String, title: String): Int =
        withContext(dispatcherIO) {
            todoDao.updateTodoTitle(todoId, title)
        }

    override suspend fun updateTodoCategory(todoId: String, category: String): Int =
        withContext(dispatcherIO) {
            todoDao.updateTodoCategory(todoId, category)
        }

    override suspend fun updateTodoDeadline(todoId: String, deadline: Long?): Int =
        withContext(dispatcherIO) {
            todoDao.updateTodoDeadline(todoId, deadline)
        }

    override suspend fun updateTodoReminder(todoId: String, reminder: Long?): Int =
        withContext(dispatcherIO) {
            todoDao.updateTodoReminder(todoId, reminder)
        }

    override suspend fun updateTodoCompletion(
        todoId: String, isComplete: Boolean, completedOn: Long?
    ): Int = withContext(dispatcherIO) {
        todoDao.updateTodoCompletion(todoId, isComplete, completedOn)
    }

    override suspend fun updateTodoTasksAvailability(
        todoId: String, tasksAvailability: Boolean
    ): Int = withContext(dispatcherIO) {
        todoDao.updateTodoTasksAvailability(todoId, tasksAvailability)
    }

    override suspend fun updateTodoNotesAvailability(
        todoId: String, notesAvailability: Boolean
    ): Int =
        withContext(dispatcherIO) { todoDao.updateTodoNotesAvailability(todoId, notesAvailability) }

    override suspend fun updateTodoAttachmentsAvailability(
        todoId: String, attachmentsAvailability: Boolean
    ): Int = withContext(dispatcherIO) {
        todoDao.updateTodoAttachmentsAvailability(todoId, attachmentsAvailability)
    }

    override fun getTodo(todoId: String): Flow<TodoEntity> =
        todoDao.getTodo(todoId).flowOn(dispatcherIO)

    override fun getTodos(): Flow<List<TodoEntity>> {
        return todoDao.getTodos().flowOn(dispatcherIO)
    }

    override fun getTodoDetails(todoId: String): Flow<TodoDetailsEntity?> {
        return todoDao.getTodoDetails(todoId).flowOn(dispatcherIO)
    }

    override suspend fun deleteTodo(todoId: String): Int =
        withContext(dispatcherIO) { todoDao.deleteTodo(todoId) }

    override suspend fun insertNote(note: NoteEntity): Long =
        withContext(dispatcherIO) { todoDao.insertNote(note) }

    override suspend fun deleteNote(noteId: String): Int =
        withContext(dispatcherIO) { todoDao.deleteNote(noteId) }

    override suspend fun insertTask(task: TaskEntity): Long =
        withContext(dispatcherIO) { todoDao.insertTask(task) }

    override suspend fun getTaskSize(): Int = withContext(dispatcherIO) {
        todoDao.getTaskSize()
    }

    override suspend fun updateTaskPosition(taskId: String, position: Int): Int =
        withContext(dispatcherIO) {
            todoDao.updateTaskPosition(taskId, position)
        }

    override suspend fun updateTaskTitle(taskId: String, title: String): Int =
        withContext(dispatcherIO) { todoDao.updateTaskTitle(taskId, title) }

    override suspend fun updateTaskCompletion(taskId: String, isComplete: Boolean) =
        withContext(dispatcherIO) {
            todoDao.updateTaskCompletion(taskId, isComplete)
        }

    override suspend fun insertTasks(tasks: List<TaskEntity>): LongArray =
        withContext(dispatcherIO) { todoDao.insertTasks(tasks) }

    override suspend fun deleteTask(taskId: String): Int =
        withContext(dispatcherIO) { todoDao.deleteTask(taskId) }

    override suspend fun insertAttachment(attachment: AttachmentEntity): Long =
        withContext(dispatcherIO) { todoDao.insertAttachment(attachment) }

    override suspend fun insertAttachments(attachments: List<AttachmentEntity>): LongArray =
        withContext(dispatcherIO) { todoDao.insertAttachments(attachments) }

    override suspend fun deleteAttachment(attachmentId: String): Int =
        withContext(dispatcherIO) { todoDao.deleteAttachment(attachmentId) }

    override suspend fun insertTodoCategory(todoCategory: TodoCategoryEntity): Long =
        withContext(dispatcherIO) { todoDao.insertTodoCategory(todoCategory) }

    override fun getTodoCategories(): Flow<List<TodoCategoryEntity>> =
        todoDao.getTodoCategories().flowOn(dispatcherIO)

    override suspend fun deleteTodoCategory(todoCategoryName: String): Int =
        withContext(dispatcherIO) { todoDao.deleteTodoCategory(todoCategoryName) }

    override fun getTodoAndNoteWithTodoId(todoId: String): Flow<TodoAndNoteEntity> =
        todoDao.getTodoAndNoteWithTodoId(todoId).flowOn(dispatcherIO)

    override fun getTodoWithTasks(todoId: String): Flow<TodoWithTasksEntity> =
        todoDao.getTodoWithTasks(todoId).flowOn(dispatcherIO)

    override fun getTodoWithAttachments(todoId: String): Flow<TodoWithAttachmentsEntity> =
        todoDao.getTodoWithAttachments(todoId).flowOn(dispatcherIO)

    override fun getTodoCategoryWithTodos(todoCategoryName: String): Flow<List<TodoCategoryWithTodosEntity>> =
        todoDao.getTodoCategoryWithTodos(todoCategoryName).flowOn(dispatcherIO)

    override fun getTodoCategoriesWithTodos(): Flow<List<TodoCategoryWithTodosEntity>> =
        todoDao.getTodoCategoriesWithTodos().flowOn(dispatcherIO)

}