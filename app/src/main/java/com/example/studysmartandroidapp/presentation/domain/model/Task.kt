package com.example.studysmartandroidapp.presentation.domain.model

data class Task(
    val taskId: Int,
    val taskSubjectId: Int,
    val title: String,
    val description: String,
    val dueDate: Long,
    val priority: Int,
    val relatedSubject: String,
    val isComplete: Boolean
)
