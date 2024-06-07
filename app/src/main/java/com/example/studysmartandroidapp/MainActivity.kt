package com.example.studysmartandroidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.studysmartandroidapp.presentation.dashboard.DashboardScreen
import com.example.studysmartandroidapp.presentation.domain.model.Session
import com.example.studysmartandroidapp.presentation.domain.model.Subject
import com.example.studysmartandroidapp.presentation.domain.model.Task
import com.example.studysmartandroidapp.presentation.subject.SubjectScreen
import com.example.studysmartandroidapp.presentation.theme.StudySmartAndroidAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudySmartAndroidAppTheme {
                //DashboardScreen()
                SubjectScreen(subjects[0])
            }
        }
    }
}

val subjects = listOf(
    Subject(subjectId = 0, subjectName = "English", goalStudyHours = 10f, colors = Subject.subjectCardColors[0]),
    Subject(subjectId = 0, subjectName = "Physics", goalStudyHours = 10f, colors = Subject.subjectCardColors[1]),
    Subject(subjectId = 0, subjectName = "Maths", goalStudyHours = 10f, colors = Subject.subjectCardColors[2]),
    Subject(subjectId = 0, subjectName = "Geology", goalStudyHours = 10f, colors = Subject.subjectCardColors[3]),
    Subject(subjectId = 0, subjectName = "Fine Arts", goalStudyHours = 10f, colors = Subject.subjectCardColors[4])
)

val tasks = listOf(
    Task(
        taskId = 1,
        taskSubjectId = 0,
        title = "Prepare notes",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedSubject = "",
        isComplete = false
    ),
    Task(
        taskId = 1,
        taskSubjectId = 0,
        title = "Do Homework",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedSubject = "",
        isComplete = true
    ),
    Task(
        taskId = 1,
        taskSubjectId = 0,
        title = "Go Cooking",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedSubject = "",
        isComplete = false
    ),
    Task(
        taskId = 1,
        taskSubjectId = 0,
        title = "Assignment",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedSubject = "",
        isComplete = false
    ),
    Task(
        taskId = 1,
        taskSubjectId = 0,
        title = "Write Poem",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedSubject = "",
        isComplete = true
    )
)

val sessions = listOf(
    Session(
        sessionId = 0,
        sessionSubjectId = 0,
        relatedSubject = "English",
        date = 0L,
        duration = 2
    ),
    Session(
        sessionId = 0,
        sessionSubjectId = 0,
        relatedSubject = "Maths",
        date = 0L,
        duration = 2
    ),
    Session(
        sessionId = 0,
        sessionSubjectId = 0,
        relatedSubject = "Physics",
        date = 0L,
        duration = 2
    ),
    Session(
        sessionId = 0,
        sessionSubjectId = 0,
        relatedSubject = "Psychology",
        date = 0L,
        duration = 2
    ),
    Session(
        sessionId = 0,
        sessionSubjectId = 0,
        relatedSubject = "Computer Science",
        date = 0L,
        duration = 2
    ),
    Session(
        sessionId = 0,
        sessionSubjectId = 0,
        relatedSubject = "Biology",
        date = 0L,
        duration = 2
    )
)