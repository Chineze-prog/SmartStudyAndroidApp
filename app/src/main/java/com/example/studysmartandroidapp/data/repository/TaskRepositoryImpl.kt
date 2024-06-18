package com.example.studysmartandroidapp.data.repository

import com.example.studysmartandroidapp.data.local.TaskDao
import com.example.studysmartandroidapp.domain.model.Task
import com.example.studysmartandroidapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
): TaskRepository {
    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskId: Int) {
        TODO("Not yet implemented")
    }

    override fun getUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>> {
        TODO("Not yet implemented")
    }

    override fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>> {
        TODO("Not yet implemented")
    }

    override fun getAllUpcomingTasks(): Flow<List<Task>> {
        // we have to filter out incomplete tasks since getAllTasks() returns all tasks
        return taskDao.getAllTasks()
    }
}