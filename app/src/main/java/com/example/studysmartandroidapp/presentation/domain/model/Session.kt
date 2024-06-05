package com.example.studysmartandroidapp.presentation.domain.model

data class Session(
    val sessionId: Int,
    val sessionSubjectId: Int,
    val relatedSubject: String,
    val date: Long,
    val duration: Long
)
