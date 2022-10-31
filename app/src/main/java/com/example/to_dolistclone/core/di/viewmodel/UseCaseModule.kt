package com.example.to_dolistclone.core.di.viewmodel

import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.create_update.*
import com.example.to_dolistclone.feature.detail.domain.abstraction.delete.*
import com.example.to_dolistclone.feature.detail.domain.implementation.create_update.*
import com.example.to_dolistclone.feature.detail.domain.implementation.delete.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideInsertAttachment(todoRepository: TodoRepository): InsertAttachment =
        InsertAttachmentImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideInsertAttachments(todoRepository: TodoRepository): InsertAttachments =
        InsertAttachmentsImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideInsertNote(todoRepository: TodoRepository): InsertNote =
        InsertNoteImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideInsertSubTasks(todoRepository: TodoRepository): InsertSubTasks =
        InsertSubTasksImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideInsertTodo(todoRepository: TodoRepository): InsertTodo =
        InsertTodoImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideInsertTodoCategory(todoRepository: TodoRepository): InsertTodoCategory =
        InsertTodoCategoryImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideDeleteAttachment(todoRepository: TodoRepository): DeleteAttachment =
        DeleteAttachmentImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideDeleteNote(todoRepository: TodoRepository): DeleteNote =
        DeleteNoteImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideDeleteTask(todoRepository: TodoRepository): DeleteTask =
        DeleteTaskImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideDeleteTodo(todoRepository: TodoRepository): DeleteTodo =
        DeleteTodoImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideDeleteTodoCategory(todoRepository: TodoRepository): DeleteTodoCategory =
        DeleteTodoCategoryImpl(todoRepository)
}