package com.example.studysmartandroidapp.presentation.session

import com.example.studysmartandroidapp.domain.model.Session
import com.example.studysmartandroidapp.domain.model.Subject

data class SessionState(
    val subjects:List<Subject> = emptyList(),
    val sessions:List<Session> = emptyList(),
    val relatedSubject: String? = null,
    val subjectId: Int? = null,
    val session: Session? = null
)
