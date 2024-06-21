package com.example.studysmartandroidapp.presentation.session

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.studysmartandroidapp.MainActivity
import com.example.studysmartandroidapp.utils.Constants.CLICK_REQUEST_CODE

// this function will be used whenever you need to pass the action to the service class
object ServiceHelper {
    fun triggerForegroundService(context: Context, action: String){
        Intent(context, StudySessionTimerService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }

    fun clickPendingIntent(context: Context): PendingIntent {
        // deep link intent - so that when the notification is clicked it will lead to a specific
        // page (the session screen)
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW, //need to view a particular screen
            "study_smart://dashboard/session".toUri(),
            context,
            MainActivity::class.java
        )

        // we're using the TSB because the hierarchy needs to be maintained, so if the user clicks
        // the notification and goes to the session screen, when the click the back button it should
        // still lead them to the dashboard screen
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(
                CLICK_REQUEST_CODE,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}