package com.example.to_dolistclone.core.di.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.to_dolistclone.core.common.ReminderNotificationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    @ActivityScoped
    fun provideFragmentManager(@ActivityContext context: Context): FragmentManager =
        (context as AppCompatActivity).supportFragmentManager

    @Provides
    @ActivityScoped
    fun provideNotificationManager(@ActivityContext context: Context): ReminderNotificationService = ReminderNotificationService(context)

}