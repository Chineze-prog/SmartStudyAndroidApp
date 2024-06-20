package com.example.studysmartandroidapp.presentation.subject

import android.os.Bundle
import androidx.navigation.NavArgs
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val subjectScreenRoute = "subject/{subjectId}"

fun NavGraphBuilder.subjectScreen(navController: NavController){
    composable(
        route = subjectScreenRoute,
        arguments = listOf(
            navArgument("subjectId"){
                type = NavType.IntType
                defaultValue = -1
            }
        )
    ){backStackEntry ->
        val args = SubjectScreenArgs.fromBundle(backStackEntry.arguments ?: Bundle())
        val subjectId = args.subjectId

        //if the subjectId is not a valid int the navigate back
        if(subjectId == -1) {
            navController.popBackStack()
            return@composable
        }

        SubjectScreenRoute(navController = navController)
    }
}

fun NavController.navigateToSubject(subjectId: Int){
    this.navigate("subject/$subjectId")
}

data class SubjectScreenArgs(val subjectId: Int): NavArgs{
    companion object{
        @JvmStatic
        fun fromBundle(bundle: Bundle): SubjectScreenArgs{
            return SubjectScreenArgs(bundle.getInt("subjectId", -1))
        }
    }
}
