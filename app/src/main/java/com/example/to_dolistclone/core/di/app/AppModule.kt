package com.example.to_dolistclone.core.di.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.to_dolistclone.core.di.coroutine_dispatchers.CoroutinesQualifiers
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.data.local.TodoDatabase
import com.example.to_dolistclone.core.data.local.abstraction.LocalDataSource
import com.example.to_dolistclone.core.data.local.dao.TodoDao
import com.example.to_dolistclone.core.data.local.implementation.LocalDataSourceImpl
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.core.repository.implementation.TodoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
        dataStore: DataStore<Preferences>,
        @CoroutinesQualifiers.IoDispatcher dispatcherIo: CoroutineDispatcher
    ): LocalDataSource = LocalDataSourceImpl(todoDao, dataStore, dispatcherIo)

    @Provides
    @Singleton
    fun provideTodoRepository(local: LocalDataSource): TodoRepository = TodoRepositoryImpl(local)

    @Provides
    @Singleton
    fun provideDateUtil(): DateUtil = DateUtil()

    const val TODO_PREFERENCES_NAME = "todo_preferences"

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }),
            migrations = listOf(SharedPreferencesMigration(appContext, TODO_PREFERENCES_NAME)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(TODO_PREFERENCES_NAME) })
    }

}