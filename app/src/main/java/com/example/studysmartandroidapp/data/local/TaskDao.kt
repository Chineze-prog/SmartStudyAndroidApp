package com.example.studysmartandroidapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.studysmartandroidapp.domain.model.Subject
import com.example.studysmartandroidapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    //either insert the subject if the subject doesn't exist or update it if it does
    @Upsert
    suspend fun upsertTask(task: Task)

    @Query("SELECT * FROM TASK WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    @Query("DELETE FROM TASK WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Query("DELETE FROM TASK WHERE taskSubjectId = :subjectId")
    suspend fun deleteTaskBySubjectId(subjectId: Int)

    @Query("SELECT * FROM TASK WHERE taskSubjectId = :subjectId")
    fun getTasksForSubject(subjectId: Int): Flow<List<Task>>

    @Query("SELECT * FROM TASK")
    fun getAllTasks(): Flow<List<Task>>
}