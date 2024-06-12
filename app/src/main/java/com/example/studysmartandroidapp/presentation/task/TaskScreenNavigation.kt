package com.example.studysmartandroidapp.presentation.task

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val taskScreenRoute = "task?subjectId={subjectId}&taskId={taskId}"

fun NavGraphBuilder.taskScreen(navController: NavController){
    composable(
        route = taskScreenRoute,
        arguments = listOf(
            navArgument("subjectId"){
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument("taskId"){
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ){backStackEntry ->
        TaskScreenRoute(
            navController = navController,
            subjectId = backStackEntry.arguments?.getString("subjectId")?.toInt(),
            taskId = backStackEntry.arguments?.getString("taskId")?.toInt()
        )
    }
}

fun NavController.navigateToTask(subjectId: Int?, taskId: Int?){
    val route = buildString {
        append("task?")

        subjectId?.let{
            append("subjectId=$it")
        }

        if(subjectId != null && taskId != null){
            append("&")
        }

        taskId?.let{
            append("taskId=$it")
        }
    }

    this.navigate(route)
}