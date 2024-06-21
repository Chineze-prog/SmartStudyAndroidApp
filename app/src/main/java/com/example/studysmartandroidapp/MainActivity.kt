package com.example.studysmartandroidapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import com.example.studysmartandroidapp.domain.model.Session
import com.example.studysmartandroidapp.domain.model.Subject
import com.example.studysmartandroidapp.presentation.navigation.StudySmartNavGraph
import com.example.studysmartandroidapp.presentation.session.StudySessionTimerService
import com.example.studysmartandroidapp.presentation.theme.StudySmartAndroidAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.ZoneOffset

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var isBound by mutableStateOf(false)

    private lateinit var timerService: StudySessionTimerService

    private val connection =
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as StudySessionTimerService.StudySessionTimerBinder
                timerService = binder.getService()
                isBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBound = false
            }
        }

    // on start of the main activity, we will bind the service
    override fun onStart() {
        super.onStart()
        Intent(this, StudySessionTimerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // on create we will wrap the content if an if condition
        setContent {
            if (isBound) {
                StudySmartAndroidAppTheme { StudySmartNavGraph(timerService = timerService) }
            }
        }

        requestPermission()
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    // on stop we are unbinding the service
    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }
}

val subjects =
    listOf(
        Subject(
            subjectId = 1,
            subjectName = "English",
            goalStudyHours = 10f,
            colors = Subject.subjectCardColors[0].map { it.toArgb() }
        ),
        Subject(
            subjectId = 2,
            subjectName = "Physics",
            goalStudyHours = 10f,
            colors = Subject.subjectCardColors[1].map { it.toArgb() }
        ),
        Subject(
            subjectId = 3,
            subjectName = "Maths",
            goalStudyHours = 10f,
            colors = Subject.subjectCardColors[2].map { it.toArgb() }
        ),
        Subject(
            subjectId = 4,
            subjectName = "Geology",
            goalStudyHours = 10f,
            colors = Subject.subjectCardColors[3].map { it.toArgb() }
        ),
        Subject(
            subjectId = 5,
            subjectName = "Computer Science",
            goalStudyHours = 10f,
            colors = Subject.subjectCardColors[4].map { it.toArgb() }
        )
    )

val sessions =
    listOf(
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
            date =
                (LocalDate.of(2024, 11, 25))
                    .atStartOfDay()
                    .toInstant(ZoneOffset.UTC)
                    .toEpochMilli(),
            duration = 2
        ),
        Session(
            sessionId = 3,
            sessionSubjectId = 2,
            relatedSubject = "Physics",
            date =
                (LocalDate.of(2024, 7, 30)).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
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
            date =
                (LocalDate.of(2024, 8, 19)).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
            duration = 2
        ),
        Session(
            sessionId = 6,
            sessionSubjectId = 4,
            relatedSubject = "Geology",
            date =
                (LocalDate.of(2024, 9, 20)).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
            duration = 2
        )
    )
