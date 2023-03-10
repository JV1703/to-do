package com.example.to_dolistclone.core.di.viewmodel


import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.calendar.domain.abstraction.CalendarTodoUseCase
import com.example.to_dolistclone.feature.calendar.domain.implementation.CalendarTodoUseCaseImpl
import com.example.to_dolistclone.feature.detail.domain.abstraction.*
import com.example.to_dolistclone.feature.detail.domain.implementation.*
import com.example.to_dolistclone.feature.home.domain.abstraction.HomeTodoCategoryUseCase
import com.example.to_dolistclone.feature.home.domain.implementation.HomeTodoCategoryUseCaseImpl
import com.example.to_dolistclone.feature.profile.domain.abstraction.ProfileTodoUseCase
import com.example.to_dolistclone.feature.profile.domain.implementation.ProfileTodoUseCaseImpl
import com.example.to_dolistclone.feature.todo.domain.abstraction.TodoUseCase
import com.example.to_dolistclone.feature.todo.domain.implementation.TodoUseCaseImpl
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
    fun provideAttachment(todoRepository: TodoRepository): DetailAttachmentUseCase =
        DetailAttachmentUseCaseImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideNoteUseCase(todoRepository: TodoRepository): DetailNoteUseCase =
        DetailNoteUseCaseImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideTaskUseCase(todoRepository: TodoRepository): DetailTaskUseCase =
        DetailTaskUseCaseImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideTodoCategoryUseCase(todoRepository: TodoRepository): TodoCategoryUseCase =
        TodoCategoryUseCaseImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideDetailTodoUseCase(todoRepository: TodoRepository): DetailTodoUseCase =
        DetailTodoUseCaseImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideTodoUseCase(todoRepository: TodoRepository, dateUtil: DateUtil): TodoUseCase =
        TodoUseCaseImpl(todoRepository, dateUtil)

    @Provides
    @ViewModelScoped
    fun provideHomeTodoCategoryUseCase(todoRepository: TodoRepository): HomeTodoCategoryUseCase =
        HomeTodoCategoryUseCaseImpl(todoRepository)

    @Provides
    @ViewModelScoped
    fun provideProfileTodoUseCase(
        todoRepository: TodoRepository,
        dateUtil: DateUtil
    ): ProfileTodoUseCase = ProfileTodoUseCaseImpl(todoRepository, dateUtil)

    @Provides
    @ViewModelScoped
    fun provideCalendarTodoUseCase(
        todoRepository: TodoRepository,
        dateUtil: DateUtil
    ): CalendarTodoUseCase = CalendarTodoUseCaseImpl(todoRepository, dateUtil)

}