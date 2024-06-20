package com.example.studysmartandroidapp.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.example.studysmartandroidapp.domain.model.Session
import com.example.studysmartandroidapp.domain.model.Subject

// states are the values that can change while a user is using the app

data class DashboardState(
    val totalSubjectCount: Int = 0,
    val totalStudiedHours: Float = 0f,
    val totalGoalStudyHours: Float = 0f,
    val subjects: List<Subject> = emptyList(),
    val subjectName: String = "",
    val goalStudyHours: String = "",
    val subjectCardColors: List<Color> = Subject.subjectCardColors.random(),
    val session: Session? = null
)
