package com.example.to_dolistclone.core.di.app

import com.example.core.di.CoroutinesQualifiers
import com.example.to_dolistclone.core.data.local.TodoDatabase
import com.example.to_dolistclone.core.data.local.abstraction.LocalDataSource
import com.example.to_dolistclone.core.data.local.dao.TodoDao
import com.example.to_dolistclone.core.data.local.implementation.LocalDataSourceImpl
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.core.repository.implementation.TodoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTodoDao(database: TodoDatabase): TodoDao = database.todoDao()

    @Provides
    @Singleton
    fun provideLocalDataSource(
        todoDao: TodoDao,
        @CoroutinesQualifiers.IoDispatcher dispatcherIo: CoroutineDispatcher
    ): LocalDataSource = LocalDataSourceImpl(todoDao, dispatcherIo)

    @Provides
    @Singleton
    fun provideTodoRepository(local: LocalDataSource): TodoRepository = TodoRepositoryImpl(local)

}