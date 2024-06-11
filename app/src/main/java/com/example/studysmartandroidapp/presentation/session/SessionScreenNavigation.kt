package com.example.studysmartandroidapp.presentation.session

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

private const val sessionScreenRoute = "session"

fun NavGraphBuilder.sessionScreen(navController: NavController){
    composable(sessionScreenRoute){
        SessionScreenRoute(navController)
    }
}