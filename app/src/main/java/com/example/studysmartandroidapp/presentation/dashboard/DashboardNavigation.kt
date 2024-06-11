package com.example.studysmartandroidapp.presentation.dashboard

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

private const val dashboardScreenRoute = "dashboard"

fun NavGraphBuilder.dashboardScreen(navController: NavController){
    composable(dashboardScreenRoute){
        DashboardScreenRoute(navController)
    }
}