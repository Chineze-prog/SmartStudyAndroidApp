package com.example.studysmartandroidapp.presentation.task

import android.os.Bundle
import androidx.navigation.NavArgs
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val taskScreenRoute = "task?subjectId={subjectId}&taskId={taskId}"

fun NavGraphBuilder.taskScreen(navController: NavController) {
    composable(
        route = taskScreenRoute,
        arguments =
            listOf(
                navArgument("subjectId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("taskId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
    ) { backStackEntry ->
        val args = TaskScreenArgs.fromBundle(backStackEntry.arguments ?: Bundle())
        val subjectId = args.subjectId
        val taskId = args.taskId

        if (taskId == -1) {
            navController.popBackStack()
            return@composable
        }

        TaskScreenRoute(navController = navController, subjectId = subjectId)
    }
}

fun NavController.navigateToTask(subjectId: Int?, taskId: Int?) {
    val route = buildString {
        append("task?")

        if (subjectId != null) {
            append("subjectId=$subjectId")

            if (taskId != null) {
                append("&")
            }
        }

        if (taskId != null) {
            append("taskId=$taskId")
        }
    }

    this.navigate(route)
}

data class TaskScreenArgs(val subjectId: Int?, val taskId: Int?) : NavArgs {
    companion object {
        @JvmStatic
        fun fromBundle(bundle: Bundle): TaskScreenArgs {
            return TaskScreenArgs(
                subjectId = bundle.getInt("subjectId").takeIf { it != -1 },
                taskId = bundle.getInt("taskId").takeIf { it != -1 }
            )
        }
    }
}
