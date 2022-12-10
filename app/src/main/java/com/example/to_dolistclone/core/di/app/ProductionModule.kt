package com.example.to_dolistclone.core.di.app

import android.content.Context
import androidx.room.Room
import com.example.to_dolistclone.core.data.local.TodoDatabase
import com.example.to_dolistclone.core.data.local.TodoDatabase.Companion.DATABASE_NAME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
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
    ): TodoDatabase = Room.databaseBuilder(context, TodoDatabase::class.java, DATABASE_NAME)
        .fallbackToDestructiveMigration().build()


    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

}