package com.example.to_dolistclone.core.di.viewmodel


import androidx.work.WorkManager
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.common.worker.WorkerManager
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
    fun provideAttachment(
        todoRepository: TodoRepository,
        detailTodoUseCase: DetailTodoUseCase,
        workerManager: WorkerManager,
        workManager: WorkManager
    ): DetailAttachmentUseCase =
        DetailAttachmentUseCaseImpl(todoRepository, detailTodoUseCase, workerManager, workManager)

    @Provides
    @ViewModelScoped
    fun provideNoteUseCase(
        todoRepository: TodoRepository,
        detailTodoUseCase: DetailTodoUseCase,
        workerManager: WorkerManager
    ): DetailNoteUseCase = DetailNoteUseCaseImpl(todoRepository, detailTodoUseCase, workerManager)

    @Provides
    @ViewModelScoped
    fun provideTaskUseCase(
        todoRepository: TodoRepository,
        detailTodoUseCase: DetailTodoUseCase,
        workerManager: WorkerManager
    ): DetailTaskUseCase = DetailTaskUseCaseImpl(todoRepository, detailTodoUseCase, workerManager)

    @Provides
    @ViewModelScoped
    fun provideTodoCategoryUseCase(
        todoRepository: TodoRepository, workerManager: WorkerManager
    ): TodoCategoryUseCase = TodoCategoryUseCaseImpl(todoRepository, workerManager)

    @Provides
    @ViewModelScoped
    fun provideDetailTodoUseCase(
        todoRepository: TodoRepository, workerManager: WorkerManager
    ): DetailTodoUseCase = DetailTodoUseCaseImpl(todoRepository, workerManager)

    @Provides
    @ViewModelScoped
    fun provideTodoUseCase(
        todoRepository: TodoRepository, dateUtil: DateUtil, workerManager: WorkerManager
    ): TodoUseCase = TodoUseCaseImpl(todoRepository, dateUtil, workerManager)

    @Provides
    @ViewModelScoped
    fun provideHomeTodoCategoryUseCase(
        todoRepository: TodoRepository, workerManager: WorkerManager
    ): HomeTodoCategoryUseCase = HomeTodoCategoryUseCaseImpl(todoRepository, workerManager)

    @Provides
    @ViewModelScoped
    fun provideProfileTodoUseCase(
        todoRepository: TodoRepository, dateUtil: DateUtil
    ): ProfileTodoUseCase = ProfileTodoUseCaseImpl(todoRepository, dateUtil)

    @Provides
    @ViewModelScoped
    fun provideCalendarTodoUseCase(
        todoRepository: TodoRepository, dateUtil: DateUtil
    ): CalendarTodoUseCase = CalendarTodoUseCaseImpl(todoRepository, dateUtil)

}