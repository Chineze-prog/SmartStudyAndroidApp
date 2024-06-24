package com.example.studysmartandroidapp.presentation.session

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmartandroidapp.domain.model.Session
import com.example.studysmartandroidapp.domain.repository.SessionRepository
import com.example.studysmartandroidapp.domain.repository.SubjectRepository
import com.example.studysmartandroidapp.utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SessionViewModel
@Inject
constructor(
    subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SessionState())

    val state =
        combine(_state, subjectRepository.getAllSubjects(), sessionRepository.getAllSessions()) {
                state,
                subjects,
                sessions ->
                state.copy(subjects = subjects, sessions = sessions)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = SessionState()
            )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    fun onEvent(event: SessionEvent) {
        when (event) {
            SessionEvent.NotifyToUpdateSubject -> notifyToUpdateSubject()
            SessionEvent.DeleteSession -> deleteSession()
            is SessionEvent.OnDeleteSessionButtonClick -> {
                _state.update { sessionState -> sessionState.copy(session = event.session) }
            }
            is SessionEvent.OnRelatedSubjectChange -> {
                _state.update { sessionState ->
                    sessionState.copy(
                        relatedSubject = event.subject.subjectName,
                        subjectId = event.subject.subjectId
                    )
                }
            }
            is SessionEvent.SaveSession -> insertSession(event.duration)
            is SessionEvent.UpdateSubjectIdAndRelatedSubject -> {
                _state.update { sessionState ->
                    sessionState.copy(
                        relatedSubject = event.relatedSubject,
                        subjectId = event.subjectId
                    )
                }
            }
        }
    }

    private fun notifyToUpdateSubject() {
        viewModelScope.launch {
            if (state.value.subjectId == null || state.value.relatedSubject == null) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Please select the subject this study session is related to."
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

    private fun insertSession(duration: Long) {
        viewModelScope.launch {
            // no session should be less than 36 seconds because it wll be converted to 0 hrs
            if (duration < 36) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message =
                            "A single session cannot be less than 36 seconds." +
                                "\n Please study some more!!"
                    )
                )
                return@launch // so that the code below will not be performed
            }

            try {
                val currentDate =
                    LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

                sessionRepository.insertSession(
                    session =
                        Session(
                            sessionSubjectId = state.value.subjectId ?: -1,
                            relatedSubject = state.value.relatedSubject ?: "",
                            date = currentDate,
                            duration = duration
                        )
                )

                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(message = "Session saved successfully.")
                )
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Couldn't save session.\n ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }
}
