package com.example.studysmartandroidapp.presentation.dashboard

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.studysmartandroidapp.presentation.session.StudySessionTimerService

private const val dashboardScreenRoute = "dashboard"

fun NavGraphBuilder.dashboardScreen(
    navController: NavController,
    timerService: StudySessionTimerService) {
    composable(dashboardScreenRoute) { DashboardScreenRoute(navController, timerService) }
}
