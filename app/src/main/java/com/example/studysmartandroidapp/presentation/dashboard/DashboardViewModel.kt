package com.example.studysmartandroidapp.presentation.dashboard

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmartandroidapp.domain.model.Subject
import com.example.studysmartandroidapp.domain.repository.SessionRepository
import com.example.studysmartandroidapp.domain.repository.SubjectRepository
import com.example.studysmartandroidapp.utils.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel is a part of the android architecture components and serves as a bridge b/w the
// repository and the UI layer
// It is responsible for managing UI related data and its state
// It observes changes in the data from the repository and provided the data to the UI

@HiltViewModel
// puts a spacial mark on a class that helps dagger Hilt understand that this class
// is a ViewModel
class DashboardViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository
): ViewModel() {
    private val _state = MutableStateFlow(DashboardState())

    // combine - takes multiple flow states and combines them into a single flow state
    val state = combine(
        _state,
        subjectRepository.getTotalSubjectCount(),
        subjectRepository.getTotalGoalHours(),
        subjectRepository.getAllSubjects(),
        sessionRepository.getTotalSessionsDuration()
    ){
        state, subjectCount, goalHours, subjects, totalSessionDuration ->

        state.copy(

            totalSubjectCount = subjectCount,
            totalGoalStudyHours = goalHours,
            subjects = subjects,
            totalStudiedHours = totalSessionDuration.toHours()
            // convert the counted seconds into hours to display to the user
        )
    }.stateIn(
        // using ViewModelScope indicates that the state is tied to the view model, so it will be
        // automatically cancelled when the associated view model is cleared or no longer in use
        scope = viewModelScope,
        // while subscribed means that the state should continue emitting values as long as there is a
        // subscriber and if there isn't a subscriber after 5 secs it should stop emitting updates
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState()
    )

    //managing the events
    fun onEvent(event: DashboardEvent){
        when(event){
            DashboardEvent.DeleteSession -> {
            }

            is DashboardEvent.OnDeleteSessionButtonClick -> {
                _state.update { dashboardState ->
                    dashboardState.copy(session = event.session)
                }
            }

            is DashboardEvent.OnGoalStudyHoursChange -> {
                _state.update { dashboardState ->
                    dashboardState.copy(goalStudyHours = event.hours)
                }
            }

            is DashboardEvent.OnSubjectCardColorChange -> {
                _state.update { dashboardState ->
                    dashboardState.copy(subjectCardColors = event.colors)
                }
            }

            is DashboardEvent.OnSubjectNameChange -> {
               _state.update { dashboardState ->
                    dashboardState.copy(subjectName = event.name)
                }
            }

            is DashboardEvent.OnTaskIsCompleteChange -> {
                TODO()
            }

            DashboardEvent.SaveSubject -> {
                saveSubject()
            }
        }
    }

    private fun saveSubject() {
        viewModelScope.launch {
            subjectRepository.upsertSubject(
                subject = Subject(
                    subjectName = state.value.subjectName,
                    goalStudyHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                    colors = state.value.subjectCardColors.map { color -> color.toArgb() }
                )
            )
        }

        _state.update { dashboardState ->
            dashboardState.copy(
                subjectName = "",
                goalStudyHours = "",
                subjectCardColors = Subject.subjectCardColors.random()
            )
        }
    }
}