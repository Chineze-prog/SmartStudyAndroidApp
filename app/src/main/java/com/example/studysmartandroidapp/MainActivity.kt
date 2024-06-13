package com.example.studysmartandroidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.studysmartandroidapp.presentation.domain.model.Session
import com.example.studysmartandroidapp.presentation.domain.model.Subject
import com.example.studysmartandroidapp.presentation.domain.model.Task
import com.example.studysmartandroidapp.presentation.navigation.StudySmartNavGraph
import com.example.studysmartandroidapp.presentation.theme.StudySmartAndroidAppTheme
import java.time.LocalDate
import java.time.ZoneOffset

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudySmartAndroidAppTheme {
                StudySmartNavGraph()
            }
        }
    }
}

val subjects = listOf(
    Subject(subjectId = 1, subjectName = "English", goalStudyHours = 10f, colors = Subject.subjectCardColors[0]),
    Subject(subjectId = 2, subjectName = "Physics", goalStudyHours = 10f, colors = Subject.subjectCardColors[1]),
    Subject(subjectId = 3, subjectName = "Maths", goalStudyHours = 10f, colors = Subject.subjectCardColors[2]),
    Subject(subjectId = 4, subjectName = "Geology", goalStudyHours = 10f, colors = Subject.subjectCardColors[3]),
    Subject(subjectId = 5, subjectName = "Computer Science", goalStudyHours = 10f, colors = Subject.subjectCardColors[4])
)

val tasks = listOf(
    Task(
        taskId = 1,
        taskSubjectId = 0,
        title = "Prepare notes",
        description = "",
        dueDate = (LocalDate.of(2024, 7, 30))
            .atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        priority = 0,
        relatedSubject = "",
        isComplete = false
    ),
    Task(
        taskId = 2,
        taskSubjectId = 0,
        title = "Do Homework",
        description = "",
        dueDate = (LocalDate.of(2024, 11, 25))
            .atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        priority = 1,
        relatedSubject = "",
        isComplete = true
    ),
    Task(
        taskId = 3,
        taskSubjectId = 0,
        title = "Go Cooking",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedSubject = "",
        isComplete = false
    ),
    Task(
        taskId = 4,
        taskSubjectId = 0,
        title = "Assignment",
        description = "",
        dueDate = (LocalDate.of(2024, 11, 25))
            .atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        priority = 1,
        relatedSubject = "",
        isComplete = false
    ),
    Task(
        taskId = 5,
        taskSubjectId = 0,
        title = "Write Poem",
        description = "",
        dueDate = (LocalDate.of(2024, 7, 30))
            .atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        priority = 0,
        relatedSubject = "",
        isComplete = true
    )
)

val sessions = listOf(
    Session(
        sessionId = 1,
        sessionSubjectId = 1,
        relatedSubject = "English",
        date = 0L,
        duration = 2
    ),
    Session(
        sessionId = 2,
        sessionSubjectId = 3,
        relatedSubject = "Maths",
        date = (LocalDate.of(2024, 11, 25))
            .atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        duration = 2
    ),
    Session(
        sessionId = 3,
        sessionSubjectId = 2,
        relatedSubject = "Physics",
        date = (LocalDate.of(2024, 7, 30))
            .atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        duration = 2
    ),
    Session(
        sessionId = 4,
        sessionSubjectId = 4,
        relatedSubject = "Maths",
        date = 0L,
        duration = 2
    ),
    Session(
        sessionId = 5,
        sessionSubjectId = 5,
        relatedSubject = "Computer Science",
        date = (LocalDate.of(2024, 8, 19))
            .atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        duration = 2
    ),
    Session(
        sessionId = 6,
        sessionSubjectId = 4,
        relatedSubject = "Geology",
        date = (LocalDate.of(2024, 9, 20))
            .atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        duration = 2
    )
)