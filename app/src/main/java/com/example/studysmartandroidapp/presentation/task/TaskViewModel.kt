package com.example.studysmartandroidapp.presentation.task

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmartandroidapp.domain.model.Task
import com.example.studysmartandroidapp.domain.repository.SubjectRepository
import com.example.studysmartandroidapp.domain.repository.TaskRepository
import com.example.studysmartandroidapp.utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject


// doesn't start with already saved values
// when you go back from a task to a subject it goes th dashboard instead
// doesn't automatically assign related subject even when it is added from a subject

@HiltViewModel
class TaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository
): ViewModel() {
    private val currentStudentId: Int = savedStateHandle["subjectId"] ?: -1

    private val currentTaskId: Int = savedStateHandle["taskId"] ?: -1

    private val _state = MutableStateFlow(TaskState())

    val state = combine(
        _state,
        subjectRepository.getAllSubjects()
    ){ state, subjects ->
        state.copy(subjects = subjects)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = TaskState()
    )

    //we're using mutable shared flow because it doesn't hold any initial values
    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    fun onEvent(event: TaskEvent){
        when(event){
            TaskEvent.DeleteTask -> TODO()

            is TaskEvent.OnDateChange -> {
                _state.update { taskState ->
                    taskState.copy(dueDate = event.millis)
                }
            }

            is TaskEvent.OnDescriptionChange -> {
                _state.update { taskState ->
                    taskState.copy(description = event.description)
                }
            }

            TaskEvent.OnIsCompleteChange -> {
                //should det to the opposite of the original status
                _state.update { taskState ->
                    taskState.copy(isTaskComplete = !_state.value.isTaskComplete)
                }
            }

            is TaskEvent.OnPriorityChange -> {
                _state.update { taskState ->
                    taskState.copy(priority = event.priority)
                }
            }

            is TaskEvent.OnRelatedSubjectSelect -> {
                _state.update { taskState ->
                    taskState.copy(
                        relatedToSubject = event.subject.subjectName,
                        subjectId = event.subject.subjectId
                    )
                }
            }

            is TaskEvent.OnTitleChange -> {
                _state.update { taskState ->
                    taskState.copy(title = event.title)
                }
            }

            TaskEvent.SaveTask -> saveTask()
        }
    }

    private fun saveTask(){
        viewModelScope.launch {
            val state = _state.value

            if(state.subjectId == null || state.relatedToSubject == null){
                //else display error message
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Please select the subject related to task.",
                        duration = SnackbarDuration.Long
                    )
                )

                return@launch
            }

            try {
                taskRepository.upsertTask(
                    task = Task(
                        title = state.title,
                        description = state.description,
                        dueDate = state.dueDate
                            ?: LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant()
                                .toEpochMilli(),
                        relatedSubject = state.relatedToSubject,
                        priority = state.priority.value,
                        isComplete = state.isTaskComplete,
                        taskSubjectId = state.subjectId,
                        taskId = state.currentTaskId
                    )
                )

                //if successful display success message
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Task saved successfully.")
                )

                //if successful display success message
                _snackbarEventFlow.emit(SnackbarEvent.NavigateUp)
            }
            catch(e: Exception){
                //else display error message
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Couldn't save task.\n ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun fetchTask(){
        viewModelScope.launch {

        }
    }
}