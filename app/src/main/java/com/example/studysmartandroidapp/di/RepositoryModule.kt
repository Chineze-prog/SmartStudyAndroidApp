package com.example.studysmartandroidapp.di

import com.example.studysmartandroidapp.data.repository.SubjectRepositoryImpl
import com.example.studysmartandroidapp.data.repository.TaskRepositoryImpl
import com.example.studysmartandroidapp.domain.repository.SessionRepository
import com.example.studysmartandroidapp.domain.repository.SubjectRepository
import com.example.studysmartandroidapp.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    // used to specify that this function is used for binding an interface to an implementation
    @Binds
    abstract fun bindSubjectRepository(impl: SubjectRepositoryImpl): SubjectRepository

    @Singleton
    @Binds
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Singleton
    @Binds
    abstract fun bindSessionRepository(impl: SessionRepository): SessionRepository

}