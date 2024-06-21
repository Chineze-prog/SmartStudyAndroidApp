package com.example.studysmartandroidapp.presentation.session

import android.content.Context
import android.content.Intent

// this function will be used whenever you need to pass the action to the service class
object ServiceHelper {
    fun triggerForegroundService(context: Context, action: String){
        Intent(context, StudySessionTimerService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}