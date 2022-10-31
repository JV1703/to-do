package com.example.to_dolistclone.core.data.local.implementation

import com.example.core.di.CoroutinesQualifiers
import com.example.to_dolistclone.core.data.local.abstraction.LocalDataSource
import com.example.to_dolistclone.core.data.local.dao.TodoDao
import com.example.to_dolistclone.core.data.local.model.*
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoCategoryWithTodosEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoWithAttachmentsEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoWithTasksEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_one.TodoAndNoteEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val todoDao: TodoDao,
    @CoroutinesQualifiers.IoDispatcher private val dispatcherIO: CoroutineDispatcher
) : LocalDataSource {

    override suspend fun insertTodo(todo: TodoEntity): Long = todoDao.insertTodo(todo)

    override fun getTodo(): Flow<TodoEntity> = todoDao.getTodo().flowOn(dispatcherIO)

    override suspend fun deleteTodo(todoId: String): Int = todoDao.deleteTodo(todoId)

    override suspend fun insertNote(note: NoteEntity): Long = todoDao.insertNote(note)

    override suspend fun deleteNote(noteId: String): Int = todoDao.deleteNote(noteId)

    override suspend fun insertTasks(tasks: List<TaskEntity>): LongArray =
        todoDao.insertTasks(tasks)

    override suspend fun deleteTask(taskId: String): Int = todoDao.deleteTask(taskId)

    override suspend fun insertAttachment(attachment: AttachmentEntity): Long =
        todoDao.insertAttachment(attachment)

    override suspend fun insertAttachments(attachments: List<AttachmentEntity>): LongArray =
        todoDao.insertAttachments(attachments)

    override suspend fun deleteAttachment(attachmentId: String): Int =
        todoDao.deleteAttachment(attachmentId)

    override suspend fun insertTodoCategory(todoCategory: TodoCategoryEntity): Long =
        todoDao.insertTodoCategory(todoCategory)

    override suspend fun deleteTodoCategory(todoCategoryName: String): Int =
        todoDao.deleteTodoCategory(todoCategoryName)

    override fun getTodoAndNoteWithTodoId(todoId: String): Flow<List<TodoAndNoteEntity>> =
        todoDao.getTodoAndNoteWithTodoId(todoId).flowOn(dispatcherIO)

    override fun getTodoWithTasks(todoId: String): Flow<List<TodoWithTasksEntity>> =
        todoDao.getTodoWithTasks(todoId).flowOn(dispatcherIO)

    override fun getTodoWithAttachments(todoId: String): Flow<List<TodoWithAttachmentsEntity>> =
        todoDao.getTodoWithAttachments(todoId).flowOn(dispatcherIO)

    override fun getTodoCategoryWithTodos(todoCategoryName: String): Flow<List<TodoCategoryWithTodosEntity>> =
        todoDao.getTodoCategoryWithTodos(todoCategoryName).flowOn(dispatcherIO)

    override fun getTodoCategoriesWithTodos(): Flow<List<TodoCategoryWithTodosEntity>> =
        todoDao.getTodoCategoriesWithTodos().flowOn(dispatcherIO)
}