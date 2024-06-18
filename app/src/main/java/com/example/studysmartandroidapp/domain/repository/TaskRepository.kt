package com.example.studysmartandroidapp.domain.repository

import com.example.studysmartandroidapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun upsertTask(task: Task)

    suspend fun getTaskById(taskId: Int): Task?

    suspend fun deleteTask(taskId: Int)

    fun getUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>>

    fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>>

    fun getAllUpcomingTasks(): Flow<List<Task>>
}