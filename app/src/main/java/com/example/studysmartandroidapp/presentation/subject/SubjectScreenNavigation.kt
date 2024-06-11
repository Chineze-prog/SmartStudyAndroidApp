package com.example.studysmartandroidapp.presentation.subject

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.studysmartandroidapp.subjects

private const val subjectScreenRoute = "subject"

fun NavGraphBuilder.subjectScreen(navController: NavController){
    composable(subjectScreenRoute){
        SubjectScreenRoute(navController, subjects[0])
    }
}