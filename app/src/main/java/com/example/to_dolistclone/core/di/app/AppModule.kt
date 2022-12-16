package com.example.to_dolistclone.core.di.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.work.WorkManager
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.data.local.TodoDatabase
import com.example.to_dolistclone.core.data.local.abstraction.LocalDataSource
import com.example.to_dolistclone.core.data.local.dao.TodoDao
import com.example.to_dolistclone.core.data.local.implementation.LocalDataSourceImpl
import com.example.to_dolistclone.core.data.remote.abstraction.RemoteDataSource
import com.example.to_dolistclone.core.data.remote.firebase.abstraction.*
import com.example.to_dolistclone.core.data.remote.firebase.implementation.*
import com.example.to_dolistclone.core.data.remote.implementation.RemoteDataSourceImpl
import com.example.to_dolistclone.core.di.coroutine_dispatchers.CoroutinesQualifiers
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.core.repository.implementation.TodoRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideTodoFirestore(firestore: FirebaseFirestore): TodoFirestore =
        TodoFirestoreImpl(firestore)

    @Provides
    @Singleton
    fun provideTaskFirestore(firestore: FirebaseFirestore): TaskFirestore =
        TaskFirestoreImpl(firestore)

    @Provides
    @Singleton
    fun provideNoteFirestore(firestore: FirebaseFirestore): NoteFirestore =
        NoteFirestoreImpl(firestore)

    @Provides
    @Singleton
    fun provideAttachmentFirestore(firestore: FirebaseFirestore): AttachmentFirestore =
        AttachmentFirestoreImpl(firestore)

    @Provides
    @Singleton
    fun provideTodoCategoryFirestore(firestore: FirebaseFirestore): TodoCategoryFirestore =
        TodoCategoryFirestoreImpl(firestore)

    @Provides
    @Singleton
    fun provideLocalDataSource(
        todoDao: TodoDao,
        dataStore: DataStore<Preferences>,
        @CoroutinesQualifiers.IoDispatcher dispatcherIo: CoroutineDispatcher
    ): LocalDataSource = LocalDataSourceImpl(todoDao, dataStore, dispatcherIo)

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        todoFs: TodoFirestore,
        taskFs: TaskFirestore,
        noteFs: NoteFirestore,
        attachmentFs: AttachmentFirestore,
        todoCategoryFs: TodoCategoryFirestore,
        @CoroutinesQualifiers.IoDispatcher dispatcherIo: CoroutineDispatcher
    ): RemoteDataSource =
        RemoteDataSourceImpl(todoFs, taskFs, noteFs, attachmentFs, todoCategoryFs, dispatcherIo)

    @Provides
    @Singleton
    fun provideTodoRepository(local: LocalDataSource, remote: RemoteDataSource): TodoRepository =
        TodoRepositoryImpl(local, remote)

    @Provides
    @Singleton
    fun provideDateUtil(): DateUtil = DateUtil()

    const val TODO_PREFERENCES_NAME = "todo_preferences"

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }),
            migrations = listOf(SharedPreferencesMigration(context, TODO_PREFERENCES_NAME)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(TODO_PREFERENCES_NAME) })
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

}