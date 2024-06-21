package com.example.studysmartandroidapp.presentation.session

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

private const val sessionScreenRoute = "session"

fun NavGraphBuilder.sessionScreen(navController: NavController) {
    composable(
        route = sessionScreenRoute,
        deepLinks = listOf(navDeepLink {
            action = Intent.ACTION_VIEW
            uriPattern = "study_smart://dashboard/session"
        })
    ) {
        SessionScreenRoute(navController)
    }
}
