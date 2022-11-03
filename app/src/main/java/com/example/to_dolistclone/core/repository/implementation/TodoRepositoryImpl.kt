package com.example.to_dolistclone.core.repository.implementation

import com.example.to_dolistclone.core.data.local.abstraction.LocalDataSource
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.toTodoCategoryWithTodos
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
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(private val local: LocalDataSource) : TodoRepository {

    override suspend fun insertTodo(todo: Todo): Long = local.insertTodo(todo.toTodoEntity())

    override fun getTodo(): Flow<Todo> = local.getTodo().map { it.toTodo() }

    override suspend fun deleteTodo(todoId: String): Int = local.deleteTodo(todoId)

    override suspend fun insertNote(note: Note): Long = local.insertNote(note.toNoteEntity())

    override suspend fun deleteNote(noteId: String): Int = local.deleteNote(noteId)

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

    override fun getTodoCategories(): Flow<List<TodoCategory>> = local.getTodoCategories().map { listEntityModel ->
        listEntityModel.map { entityModel ->
            entityModel.toTodoCategory()
        }
    }

    override suspend fun deleteTodoCategory(todoCategoryName: String): Int =
        local.deleteTodoCategory(todoCategoryName)

    override fun getTodoAndNoteWithTodoId(todoId: String): Flow<List<TodoAndNote>> =
        local.getTodoAndNoteWithTodoId(todoId).map { listEntityModel ->
            listEntityModel.map { entityModel ->
                entityModel.toTodoAndNote()
            }
        }

    override fun getTodoWithTasks(todoId: String): Flow<List<TodoWithTasks>> =
        local.getTodoWithTasks(todoId).map { listEntityModel ->
            listEntityModel.map { entityModel ->
                entityModel.toTodoWithTasks()
            }
        }

    override fun getTodoWithAttachments(todoId: String): Flow<List<TodoWithAttachments>> =
        local.getTodoWithAttachments(todoId).map { listEntityModel ->
            listEntityModel.map { entityModel ->
                entityModel.toTodoWithAttachments()
            }
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

}
