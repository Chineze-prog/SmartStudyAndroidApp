package com.example.studysmartandroidapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.studysmartandroidapp.presentation.dashboard.dashboardScreen
import com.example.studysmartandroidapp.presentation.session.StudySessionTimerService
import com.example.studysmartandroidapp.presentation.session.sessionScreen
import com.example.studysmartandroidapp.presentation.subject.subjectScreen
import com.example.studysmartandroidapp.presentation.task.taskScreen

@Composable
fun StudySmartNavGraph(
    navController: NavHostController = rememberNavController(),
    timerService: StudySessionTimerService
    ) {
    NavHost(navController = navController, startDestination = "dashboard") {
        dashboardScreen(navController)
        subjectScreen(navController)
        taskScreen(navController)
        sessionScreen(navController, timerService)
    }
}
