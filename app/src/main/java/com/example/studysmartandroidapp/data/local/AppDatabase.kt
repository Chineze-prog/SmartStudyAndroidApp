package com.example.studysmartandroidapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studysmartandroidapp.domain.model.Session
import com.example.studysmartandroidapp.domain.model.Subject
import com.example.studysmartandroidapp.domain.model.Task

@Database(
    entities = [Subject::class, Task::class, Session::class],
    version = 1 //if the schema is changed in the future, this needs to be updated
)

@TypeConverters(ColorListConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun subjectDao(): SubjectDao

    abstract fun taskDao(): TaskDao

    abstract fun sessionDao(): SessionDao
}