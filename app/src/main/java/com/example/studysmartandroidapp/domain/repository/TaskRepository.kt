package com.example.studysmartandroidapp.domain.repository

import com.example.studysmartandroidapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun upsertTask(task: Task)

    suspend fun getTaskById(taskId: Int): Task?

    suspend fun deleteTask(taskId: Int)

    suspend fun deleteTaskBySubjectId(subjectId: Int)

    suspend fun getTasksForSubject(subjectId: Int): Flow<List<Task>>

    fun getAllTasks(): Flow<List<Task>>

}