package com.example.studysmartandroidapp.di

import android.app.Application
import androidx.room.Room
import com.example.studysmartandroidapp.data.local.AppDatabase
import com.example.studysmartandroidapp.data.local.SessionDao
import com.example.studysmartandroidapp.data.local.SubjectDao
import com.example.studysmartandroidapp.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// indicates that this object/class is a dagger hilt module and is responsible for defining how
// dependencies should be provided
@Module
// Specifies the component in which this module should be installed
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    // used on functions to declare that they are responsible for providing objects of dependencies
    // each Provides annotated function must return the type of the object its providing
    // .e.g. AppDatabase
    @Provides
    // used to specify the scope of the provided dependency
    // it makes sure there will be only 1 instance of the provided object throughout the application
    @Singleton
    fun provideDatabase(application: Application): AppDatabase{
        return Room
            .databaseBuilder(
            application,
            AppDatabase::class.java,
            "studysmart.db"
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideSubjectDao(database: AppDatabase): SubjectDao{
        return database.subjectDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideSessionDao(database: AppDatabase): SessionDao{
        return database.sessionDao()
    }
}