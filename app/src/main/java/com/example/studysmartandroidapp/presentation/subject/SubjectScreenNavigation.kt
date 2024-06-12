package com.example.studysmartandroidapp.presentation.subject

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.studysmartandroidapp.subjects

private const val subjectScreenRoute = "subject/subjectId={subjectId}"

fun NavGraphBuilder.subjectScreen(navController: NavController){
    composable(
        route = subjectScreenRoute,
        arguments = listOf(
            navArgument("subjectId"){
                type = NavType.StringType
                nullable = false
            }
        )){backStackEntry ->
        val subjectId = backStackEntry.arguments?.getString("subjectId")?.toIntOrNull() ?: run{
            //if the subjectId is not a valid int the navigate back
            navController.popBackStack()
            return@composable
        }
        SubjectScreenRoute(
            navController = navController,
            subjectId = subjectId
        )
    }
}

fun NavController.navigateToSubject(subjectId: Int){
    val route = buildString {
        append("subject/")

        append("subjectId=$subjectId")
    }

    this.navigate(route)
}