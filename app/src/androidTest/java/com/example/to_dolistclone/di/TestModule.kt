package com.example.to_dolistclone.di

import android.content.Context
import androidx.room.Room
import com.example.to_dolistclone.core.data.local.TodoDatabase
import com.example.to_dolistclone.core.di.app.ProductionModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [ProductionModule::class])
object TestModule {

    val FAKE_DATASTORE = "Fake datastore"

    @Provides
    fun provideInMemoryTodoDb(@ApplicationContext context: Context): TodoDatabase {
        return Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java)
            .allowMainThreadQueries().build()
    }

//    @Provides
//    fun provideTestContext(): Context = ApplicationProvider.getApplicationContext()
//
//    @Provides
//    fun provideDataStore(testContext: Context): DataStore<Preferences> =
//        PreferenceDataStoreFactory.create(scope = TestScope(),
//            produceFile = { testContext.preferencesDataStoreFile(FAKE_DATASTORE) })
//

    @Provides
    fun provideFirestoreSettings(): FirebaseFirestoreSettings {
        return FirebaseFirestoreSettings.Builder().setHost("10.0.2.2:8080").setSslEnabled(false)
            .setPersistenceEnabled(false).build()
    }

    @Provides
    fun provideFirebaseFirestore(
        firestoreSettings: FirebaseFirestoreSettings
    ): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = firestoreSettings
        return firestore
    }

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth{
        val auth = FirebaseAuth.getInstance()
        auth.useEmulator("10.0.2.2", 9099)
        return auth
    }

}