package com.example.studysmartandroidapp.presentation.dashboard

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmartandroidapp.domain.model.Session
import com.example.studysmartandroidapp.domain.model.Subject
import com.example.studysmartandroidapp.domain.model.Task
import com.example.studysmartandroidapp.domain.repository.SessionRepository
import com.example.studysmartandroidapp.domain.repository.SubjectRepository
import com.example.studysmartandroidapp.domain.repository.TaskRepository
import com.example.studysmartandroidapp.utils.SnackbarEvent
import com.example.studysmartandroidapp.utils.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel is a part of the android architecture components and serves as a bridge b/w the
// repository and the UI layer
// It is responsible for managing UI related data and its state
// It observes changes in the data from the repository and provided the data to the UI

@HiltViewModel
// puts a spacial mark on a class that helps dagger Hilt understand that this class
// is a ViewModel
class DashboardViewModel
@Inject
constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())

    // combine - takes multiple flow states and combines them into a single flow state
    // cant accept more than 5 flow values
    val state =
        combine(
                _state,
                subjectRepository.getTotalSubjectCount(),
                subjectRepository.getTotalGoalHours(),
                subjectRepository.getAllSubjects(),
                sessionRepository.getTotalSessionsDuration()
            ) { state, subjectCount, goalHours, subjects, totalSessionDuration ->
                state.copy(
                    totalSubjectCount = subjectCount,
                    totalGoalStudyHours = goalHours,
                    subjects = subjects,
                    totalStudiedHours = totalSessionDuration.toHours()
                    // convert the counted seconds into hours to display to the user
                )
            }
            .stateIn(
                // using ViewModelScope indicates that the state is tied to the view model, so it
                // will be
                // automatically cancelled when the associated view model is cleared or no longer in
                // use
                scope = viewModelScope,
                // while subscribed means that the state should continue emitting values as long as
                // there is a
                // subscriber and if there isn't a subscriber after 5 secs it should stop emitting
                // updates
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = DashboardState()
            )

    val tasks: StateFlow<List<Task>> =
        taskRepository
            .getAllUpcomingTasks()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val recentSessions: StateFlow<List<Session>> =
        sessionRepository
            .getRecentSessionsFiveSessions()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // we're using mutable shared flow because it doesn't hold any initial values
    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    // managing the events
    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.DeleteSession -> deleteSession()
            is DashboardEvent.OnDeleteSessionButtonClick -> {
                _state.update { dashboardState -> dashboardState.copy(session = event.session) }
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
                _state.update { dashboardState -> dashboardState.copy(subjectName = event.name) }
            }
            is DashboardEvent.OnTaskIsCompleteChange -> {
                updateTask(event.task)
            }
            DashboardEvent.SaveSubject -> saveSubject()
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(task = task.copy(isComplete = !task.isComplete))

                // if successful display success message
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Saved in completed tasks."))
            } catch (e: Exception) {
                // else display error message
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Couldn't update task.\n ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun saveSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    subject =
                        Subject(
                            subjectName = state.value.subjectName,
                            goalStudyHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                            colors = state.value.subjectCardColors.map { color -> color.toArgb() }
                        )
                )

                _state.update { dashboardState ->
                    dashboardState.copy(
                        subjectName = "",
                        goalStudyHours = "",
                        subjectCardColors = Subject.subjectCardColors.random()
                    )
                }

                // if successful display success message
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Subject saved successfully."))
            } catch (e: Exception) {
                // else display error message
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Couldn't save subject.\n ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun deleteSession() {
        viewModelScope.launch {
            try {
                state.value.session?.let { session -> sessionRepository.deleteSession(session) }

                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(message = "Session deleted successfully.")
                )
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Couldn't delete session.\n ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }
}
