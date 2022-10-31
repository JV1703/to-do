package com.example.to_dolistclone.core.di.app

import android.content.Context
import androidx.room.Room
import com.example.to_dolistclone.core.data.local.TodoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductionModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): TodoDatabase =
        Room.databaseBuilder(context, TodoDatabase::class.java, "todo_database").build()

}