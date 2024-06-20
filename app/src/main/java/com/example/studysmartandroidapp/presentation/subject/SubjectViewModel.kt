package com.example.studysmartandroidapp.presentation.subject

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmartandroidapp.domain.model.Subject
import com.example.studysmartandroidapp.domain.model.Task
import com.example.studysmartandroidapp.domain.repository.SessionRepository
import com.example.studysmartandroidapp.domain.repository.SubjectRepository
import com.example.studysmartandroidapp.domain.repository.TaskRepository
import com.example.studysmartandroidapp.utils.SnackbarEvent
import com.example.studysmartandroidapp.utils.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class SubjectViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val currentStudentId: Int = savedStateHandle["subjectId"] ?: -1

    private val _state = MutableStateFlow(SubjectState())

    val state =
        combine(
                _state,
                taskRepository.getUpcomingTasksForSubject(currentStudentId),
                taskRepository.getCompletedTasksForSubject(currentStudentId),
                sessionRepository.getRecentSessionsTenSessionsForSubject(currentStudentId),
                sessionRepository.getTotalSessionsDurationBySubject(currentStudentId)
            ) { state, upcomingTasks, completedTasks, recentSessions, totalSessionsDuration ->
                state.copy(
                    upcomingTasks = upcomingTasks,
                    completedTasks = completedTasks,
                    recentSessions = recentSessions,
                    studiedHours = totalSessionsDuration.toHours()
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = SubjectState()
            )

    // so that the current subject is fetched automatically
    init {
        fetchSubject()
    }

    // we're using mutable shared flow because it doesn't hold any initial values
    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    fun onEvent(event: SubjectEvent) {
        when (event) {
            SubjectEvent.DeleteSession -> {}
            SubjectEvent.DeleteSubject -> deleteSubject()
            is SubjectEvent.OnDeleteSessionButtonClick -> {}
            is SubjectEvent.OnGoalStudyHoursChange -> {
                _state.update { subjectState -> subjectState.copy(goalStudyHours = event.hours) }
            }
            is SubjectEvent.OnSubjectCardColorChange -> {
                _state.update { subjectState -> subjectState.copy(subjectCardColors = event.color) }
            }
            is SubjectEvent.OnSubjectNameChange -> {
                _state.update { subjectState -> subjectState.copy(subjectName = event.name) }
            }
            is SubjectEvent.OnTaskIsCompleteChange -> {
                updateTask(event.task)
            }
            SubjectEvent.UpdateSubject -> updateSubject()
            SubjectEvent.UpdateProgress -> {
                // coerceIn ensures the value always lies b/w 0 & 1
                val goalStudyHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f
                _state.update { subjectState ->
                    subjectState.copy(
                        progress = (state.value.studiedHours / goalStudyHours).coerceIn(0f, 1f)
                    )
                }
            }
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(task = task.copy(isComplete = !task.isComplete))

                if (!task.isComplete) {
                    _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Saved in completed tasks."))
                } else {
                    _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Saved in upcoming tasks."))
                }
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

    private fun deleteSubject() {
        viewModelScope.launch {
            try {
                val currentSubjectId = state.value.currentSubjectId

                if (currentSubjectId != null) {
                    withContext(Dispatchers.IO) {
                        subjectRepository.deleteSubject(subjectId = currentSubjectId)
                    }

                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Subject deleted successfully.")
                    )

                    _snackbarEventFlow.emit(SnackbarEvent.NavigateUp)
                } else {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "No subject to delete.")
                    )
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Couldn't delete subject.\n${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun updateSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    subject =
                        Subject(
                            subjectId = state.value.currentSubjectId,
                            subjectName = state.value.subjectName,
                            goalStudyHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                            colors = state.value.subjectCardColors.map { color -> color.toArgb() }
                        )
                )

                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(message = "Subject updated successfully.")
                )
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Couldn't update subject.\n${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun fetchSubject() {
        viewModelScope.launch {
            subjectRepository.getSubjectById(currentStudentId)?.let { subject ->
                _state.update { subjectState ->
                    subjectState.copy(
                        subjectName = subject.subjectName,
                        goalStudyHours = subject.goalStudyHours.toString(),
                        subjectCardColors = subject.colors.map { colorInt -> Color(colorInt) },
                        currentSubjectId = subject.subjectId
                    )
                }
            }
        }
    }
}
