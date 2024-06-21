package com.example.studysmartandroidapp.presentation.session

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.example.studysmartandroidapp.utils.Constants.ACTION_SERVICE_CANCEL
import com.example.studysmartandroidapp.utils.Constants.ACTION_SERVICE_START
import com.example.studysmartandroidapp.utils.Constants.ACTION_SERVICE_STOP
import com.example.studysmartandroidapp.utils.Constants.NOTIFICATION_CHANEL_ID
import com.example.studysmartandroidapp.utils.Constants.NOTIFICATION_CHANEL_NAME
import com.example.studysmartandroidapp.utils.Constants.NOTIFICATION_ID
import com.example.studysmartandroidapp.utils.pad
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds

/**
 * In android development a service class refers to a component of the android operating system that
 * allows you to perform long running operations or background tasks without the need for a user
 * interface.
 *
 * They're typically used for tasks like playing music in the background, fetching data from the
 * internet, or performing other background operations that should not be interrupted by the user's
 * interaction with the app.
 *
 * There are 2 main types of service classes in Android:
 * 1. Bond service: services that are bound to components usually activities. They allow
 *    communication b/w the service and the component that bounds to it. They're used for tasks that
 *    REQUIRE INTERACTION b/w the service and the component, like fetching data from the service and
 *    updating the UI of an activity.
 * 2. onStartCommand: services that you explicitly start and stop using startService and stopService
 *    methods. They're used for tasks that need the RUN INDEPENDENTLY for an extended period such as
 *    background music playback.
 *
 * You also need to specify this service class in your manifest file: <service
 * android:name=".presentation.session.StudySessionTimerService" android:exported="false" //this
 * attribute specifies whether the service can be assessed by //components outside of your app />
 */
@AndroidEntryPoint
class StudySessionTimerService : Service() {
    @Inject lateinit var notificationManager: NotificationManager

    @Inject lateinit var notificationBuilder: NotificationCompat.Builder

    private lateinit var timer: Timer

    private val binder = StudySessionTimerBinder()

    var duration: Duration = Duration.ZERO

    var hours = mutableStateOf("00")
        private set
    // private set makes sure this value can only be written in this file and read outside this file

    var minutes = mutableStateOf("00")
        private set

    var seconds = mutableStateOf("00")
        private set

    var currentTimerState = mutableStateOf(TimerState.IDLE)
        private set

    override fun onBind(intent: Intent?): IBinder? = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action.let { constant ->
            when (constant) {
                ACTION_SERVICE_START -> {
                    startForegroundService()
                    startTimer { hours, minutes, seconds ->
                        updateNotification(hours, minutes, seconds)
                    }
                }
                ACTION_SERVICE_STOP -> {
                    stopTimer()
                }
                ACTION_SERVICE_CANCEL -> {
                    stopTimer()
                    cancelTimer()
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        createNotificationChanel()
        // when you start your foreground service then the notification will be built
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun stopForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChanel() {
        // it is best to use low importance because the timer could be running for hours and if you
        // use
        // high importance it will also make sounds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    NOTIFICATION_CHANEL_ID,
                    NOTIFICATION_CHANEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                )

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(hours: String, minutes: String, seconds: String) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText("$hours:$minutes:$seconds").build()
        )
    }

    private fun startTimer(onTick: (h: String, m: String, s: String) -> Unit) {
        currentTimerState.value = TimerState.STARTED

        // initial time is 1sec and then with the period of 1sec the timer will be updated to the
        // next value
        timer =
            fixedRateTimer(initialDelay = 1000L, period = 1000L) {
                duration = duration.plus(1.seconds)
                updateTimeUnits()
                onTick(hours.value, minutes.value, seconds.value)
            }
    }

    private fun stopTimer() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }

        currentTimerState.value = TimerState.STOPPED
    }

    private fun cancelTimer() {
        duration = ZERO
        updateTimeUnits()
        currentTimerState.value = TimerState.IDLE
    }

    private fun updateTimeUnits() {
        duration.toComponents { hours, minutes, seconds, _ ->
            this@StudySessionTimerService.hours.value = hours.toInt().pad()
            this@StudySessionTimerService.minutes.value = minutes.pad()
            this@StudySessionTimerService.seconds.value = seconds.pad()
        }
    }

    inner class StudySessionTimerBinder : Binder() {
        fun getService(): StudySessionTimerService = this@StudySessionTimerService
    }
}

enum class TimerState {
    IDLE,
    STARTED,
    STOPPED
}
