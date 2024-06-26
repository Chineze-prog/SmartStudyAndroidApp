package com.example.studysmartandroidapp.data.repository

import com.example.studysmartandroidapp.data.local.TaskDao
import com.example.studysmartandroidapp.domain.model.Task
import com.example.studysmartandroidapp.domain.repository.TaskRepository
import com.example.studysmartandroidapp.utils.toLocalDate
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl @Inject constructor(private val taskDao: TaskDao) : TaskRepository {
    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)
    }

    override suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
    }

    override fun getUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>> {
        val currentDate =
            LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli().toLocalDate()

        return taskDao
            .getTasksForSubject(subjectId)
            .map { tasks -> tasks.filter { task -> task.isComplete.not() } }
            .map { tasks -> tasks.filter { task -> task.dueDate.toLocalDate() >= currentDate } }
            .map { tasks -> sortTasks(tasks) }
    }

    override fun getOverdueTasksForSubject(subjectId: Int): Flow<List<Task>> {
        val currentDate =
            LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli().toLocalDate()

        return taskDao
            .getTasksForSubject(subjectId)
            .map { tasks -> tasks.filter { task -> task.isComplete.not() } }
            .map { tasks -> tasks.filter { task -> task.dueDate.toLocalDate() < currentDate } }
            .map { tasks -> sortTasks(tasks) }
    }

    override fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao
            .getTasksForSubject(subjectId)
            .map { tasks -> tasks.filter { task -> task.isComplete } }
            .map { tasks -> sortTasks(tasks) }
    }

    override fun getAllUpcomingTasks(): Flow<List<Task>> {
        val currentDate =
            LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli().toLocalDate()

        // we have to filter out incomplete tasks since getAllTasks() returns all tasks
        return taskDao
            .getAllTasks()
            .map { tasks -> tasks.filter { task -> task.isComplete.not() } }
            .map { tasks -> tasks.filter { task -> task.dueDate.toLocalDate() >= currentDate } }
            .map { tasks -> sortTasks(tasks) }
    }

    override fun getAllOverdueTasks(): Flow<List<Task>> {
        val currentDate =
            LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli().toLocalDate()

        return taskDao
            .getAllTasks()
            .map { tasks -> tasks.filter { task -> task.isComplete.not() } }
            .map { tasks -> tasks.filter { task -> task.dueDate.toLocalDate() < currentDate } }
            .map { tasks -> sortTasks(tasks) }
    }

    // sort tasks based on priority and due date
    private fun sortTasks(tasks: List<Task>): List<Task> {
        return tasks.sortedWith(
            compareBy<Task> { task -> task.dueDate }.thenByDescending { task -> task.priority }
        )
    }
}
