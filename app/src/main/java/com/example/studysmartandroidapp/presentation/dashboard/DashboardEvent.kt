package com.example.studysmartandroidapp.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.example.studysmartandroidapp.domain.model.Session
import com.example.studysmartandroidapp.domain.model.Task


// events are users actions/ whatever the user can perform on the screen

/*
    A sealed class in Kotlin is a class that is marked with the sealed keyword. It is used to define
    a closed set of subclasses. A sealed class is a way to define a restricted class hierarchy where
    subclasses are predefined and finite. The subclasses of a sealed class are defined within the
    sealed class itself, and each subclass must be declared as inner or data or class, with no other
    modifiers allowed.
 */
sealed class DashboardEvent {
    // if some value is also coming through from the user's side then we use data class otherwise
    // use data object

    data object saveSubject: DashboardEvent()

    data object deleteSubject: DashboardEvent()

    data class onDeleteSessionButtonClick(val session: Session): DashboardEvent()

    data class onTaskIsCompleteChange(val task: Task): DashboardEvent()

    data class onSubjectCardColorChange(val colors: List<Color>): DashboardEvent()

    data class onSubjectNameChange(val name: String): DashboardEvent()

    data class onGoalStudyHoursChange(val hours: String): DashboardEvent()
}