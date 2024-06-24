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
import androidx.core.app.ActivityCompat
import com.example.studysmartandroidapp.presentation.navigation.StudySmartNavGraph
import com.example.studysmartandroidapp.presentation.session.StudySessionTimerService
import com.example.studysmartandroidapp.presentation.theme.StudySmartAndroidAppTheme
import dagger.hilt.android.AndroidEntryPoint

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