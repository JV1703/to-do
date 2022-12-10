package com.example.to_dolistclone.core.data.local.implementation

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.to_dolistclone.core.data.local.CacheResult
import com.example.to_dolistclone.core.data.local.abstraction.LocalDataSource
import com.example.to_dolistclone.core.data.local.dao.TodoDao
import com.example.to_dolistclone.core.data.local.implementation.PreferencesKeys.SELECTED_PIE_GRAPH_OPTION
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
import com.example.to_dolistclone.core.data.local.safeCacheCall
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
    val SELECTED_PIE_GRAPH_OPTION = intPreferencesKey("selected_pie_graph_option")
}

class LocalDataSourceImpl @Inject constructor(
    private val todoDao: TodoDao,
    private val dataStore: DataStore<Preferences>,
    @CoroutinesQualifiers.IoDispatcher private val dispatcherIO: CoroutineDispatcher
) : LocalDataSource {

    override suspend fun saveSelectedPieGraphOption(selectedOption: Int) {
        dataStore.edit { preferences ->
            preferences[SELECTED_PIE_GRAPH_OPTION] = selectedOption
        }
    }

    override fun getSelectedPieGraphOption(): Flow<Int> = dataStore.data.map { preferences ->
        preferences[SELECTED_PIE_GRAPH_OPTION] ?: 0
    }.catch { e ->
        Log.e("LocalDataSourceImpl", "getSelectedPieGraphOption - errorMsg: ${e.message}")
    }

    override suspend fun saveSelectedTodoId(todoId: String) {
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

    override fun getSelectedTodoId(): Flow<String> = dataStore.data.map { preferences ->
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

    override suspend fun insertTodo(todo: TodoEntity): CacheResult<Long?> {
        return safeCacheCall(dispatcherIO) { todoDao.insertTodo(todo) }
    }

    override suspend fun updateTodoUpdatedOn(todoId: String, updatedOn: Long) = safeCacheCall(dispatcherIO){
        todoDao.updateTodoUpdatedOn(todoId = todoId, updatedOn = updatedOn)
    }

    override suspend fun updateTodoTitle(todoId: String, title: String, updatedOn: Long): CacheResult<Int?> =
        safeCacheCall(dispatcherIO) {
            todoDao.updateTodoTitle(todoId, title, updatedOn)
        }

    override suspend fun updateTodoCategory(todoId: String, category: String, updatedOn: Long): CacheResult<Int?> =
        safeCacheCall(dispatcherIO) {
            todoDao.updateTodoCategory(todoId, category, updatedOn)
        }

    override suspend fun updateTodoDeadline(todoId: String, deadline: Long?, updatedOn: Long): CacheResult<Int?> =
        safeCacheCall(dispatcherIO) {
            todoDao.updateTodoDeadline(todoId, deadline, updatedOn)
        }

    override suspend fun updateTodoReminder(todoId: String, reminder: Long?, updatedOn: Long): CacheResult<Int?> =
        safeCacheCall(dispatcherIO) {
            todoDao.updateTodoReminder(todoId, reminder, updatedOn)
        }

    override suspend fun updateTodoCompletion(
        todoId: String, isComplete: Boolean, completedOn: Long?, updatedOn: Long
    ): CacheResult<Int?> = safeCacheCall(dispatcherIO) {
        todoDao.updateTodoCompletion(todoId, isComplete, completedOn, updatedOn)
    }

    override suspend fun updateTodoTasksAvailability(
        todoId: String, tasksAvailability: Boolean, updatedOn: Long
    ): CacheResult<Int?> = safeCacheCall(dispatcherIO) {
        todoDao.updateTodoTasksAvailability(todoId, tasksAvailability, updatedOn)
    }

    override suspend fun updateTodoNotesAvailability(
        todoId: String, notesAvailability: Boolean, updatedOn: Long
    ): CacheResult<Int?> = safeCacheCall(dispatcherIO) {
        todoDao.updateTodoNotesAvailability(todoId, notesAvailability, updatedOn)
    }


    override suspend fun updateTodoAttachmentsAvailability(
        todoId: String, attachmentsAvailability: Boolean, updatedOn: Long
    ): CacheResult<Int?> = safeCacheCall(dispatcherIO) {
        todoDao.updateTodoAttachmentsAvailability(todoId, attachmentsAvailability, updatedOn)
    }

    override suspend fun updateTodoAlarmRef(todoId: String, alarmRef: Int?, updatedOn: Long): CacheResult<Int?> =
        safeCacheCall(dispatcherIO) {
            todoDao.updateTodoAlarmRef(todoId, alarmRef, updatedOn)
        }

    override fun getTodo(todoId: String): Flow<TodoEntity> =
        todoDao.getTodo(todoId).flowOn(dispatcherIO)

    override fun getTodos(): Flow<List<TodoEntity>> = todoDao.getTodos().flowOn(dispatcherIO)

    override fun getTodoDetails(todoId: String): Flow<TodoDetailsEntity?> {
        return todoDao.getTodoDetails(todoId).flowOn(dispatcherIO)
    }

    override suspend fun deleteTodo(todoId: String): CacheResult<Int?> =
        safeCacheCall(dispatcherIO) {
            todoDao.deleteTodo(todoId)
        }

    override suspend fun insertNote(note: NoteEntity): CacheResult<Long?> =
        safeCacheCall(dispatcherIO) {
            todoDao.insertNote(note)
        }

    override fun getNote(noteId: String): Flow<NoteEntity?> = todoDao.getNote(noteId).flowOn(dispatcherIO)

    override fun getNotes(): Flow<List<NoteEntity>> = todoDao.getNotes().flowOn(dispatcherIO)

    override suspend fun deleteNote(noteId: String): CacheResult<Int?> =
        safeCacheCall(dispatcherIO) {
            todoDao.deleteNote(noteId)
        }

    override suspend fun insertTask(task: TaskEntity): CacheResult<Long?> =
        safeCacheCall(dispatcherIO) { todoDao.insertTask(task) }

    override suspend fun updateTaskPosition(taskId: String, position: Int): CacheResult<Int?> =
        safeCacheCall(dispatcherIO) {
            todoDao.updateTaskPosition(taskId, position)
        }

    override suspend fun updateTaskTitle(taskId: String, title: String): CacheResult<Int?> =
        safeCacheCall(dispatcherIO) { todoDao.updateTaskTitle(taskId, title) }

    override suspend fun updateTaskCompletion(taskId: String, isComplete: Boolean) =
        safeCacheCall(dispatcherIO) {
            todoDao.updateTaskCompletion(taskId, isComplete)
        }

    override suspend fun insertTasks(tasks: List<TaskEntity>): LongArray =
        withContext(dispatcherIO) {
            try {
                todoDao.insertTasks(tasks)
            } catch (e: Exception) {
                Log.e(this.javaClass.name, "insertTasks - errorMsg: ${e.message}")
                longArrayOf(-1)
            }
        }

    override fun getTask(taskId: String): Flow<TaskEntity?> = todoDao.getTask(taskId)

    override fun getTasks(): Flow<List<TaskEntity>> = todoDao.getTasks().flowOn(dispatcherIO)

    override suspend fun deleteTask(taskId: String): CacheResult<Int?> =
        safeCacheCall(dispatcherIO) { todoDao.deleteTask(taskId) }

    override suspend fun insertAttachment(attachment: AttachmentEntity): CacheResult<Long?> =
        safeCacheCall(dispatcherIO){
            todoDao.insertAttachment(attachment)
        }

    override suspend fun insertAttachments(attachments: List<AttachmentEntity>): CacheResult<LongArray?> =
        safeCacheCall(dispatcherIO){
            todoDao.insertAttachments(attachments)
        }

    override fun getAttachment(attachmentId: String) = todoDao.getAttachment(attachmentId).flowOn(dispatcherIO)

    override fun getAttachments(): Flow<List<AttachmentEntity>> =
        todoDao.getAttachments().flowOn(dispatcherIO)

    override suspend fun deleteAttachment(attachmentId: String): CacheResult<Int?> =
        safeCacheCall(dispatcherIO){
            todoDao.deleteAttachment(attachmentId)
        }

    override suspend fun insertTodoCategory(todoCategory: TodoCategoryEntity): CacheResult<Long?> =
        safeCacheCall(dispatcherIO) {
            todoDao.insertTodoCategory(todoCategory)
        }

    override fun getTodoCategories(): Flow<List<TodoCategoryEntity>> =
        todoDao.getTodoCategories().flowOn(dispatcherIO)

    override suspend fun deleteTodoCategory(todoCategoryName: String): Int =
        withContext(dispatcherIO) { todoDao.deleteTodoCategory(todoCategoryName) }

    override fun getTodoAndNote(todoId: String): Flow<TodoAndNoteEntity?> =
        todoDao.getTodoAndNote(todoId).flowOn(dispatcherIO)

    override fun getTodoWithTasks(todoId: String): Flow<TodoWithTasksEntity?> =
        todoDao.getTodoWithTasks(todoId).flowOn(dispatcherIO)

    override fun getTodoWithAttachments(todoId: String): Flow<TodoWithAttachmentsEntity?> =
        todoDao.getTodoWithAttachments(todoId).flowOn(dispatcherIO)

    override fun getTodoCategoryWithTodos(todoCategoryName: String): Flow<TodoCategoryWithTodosEntity?> =
        todoDao.getTodoCategoryWithTodos(todoCategoryName).flowOn(dispatcherIO)

    override fun getTodoCategoriesWithTodos(): Flow<List<TodoCategoryWithTodosEntity>> =
        todoDao.getTodoCategoriesWithTodos().flowOn(dispatcherIO)

}