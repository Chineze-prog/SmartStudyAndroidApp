package com.example.studysmartandroidapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
//tells Hilt to generate the necessary code and configurations for dependency
// injection in the android app
class StudySmartApp: Application()