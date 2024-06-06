package com.example.studysmartandroidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.studysmartandroidapp.presentation.dashboard.DashboardScreen
import com.example.studysmartandroidapp.presentation.subject.SubjectScreen
import com.example.studysmartandroidapp.presentation.theme.StudySmartAndroidAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudySmartAndroidAppTheme {
                //DashboardScreen()
                SubjectScreen()
            }
        }
    }
}
