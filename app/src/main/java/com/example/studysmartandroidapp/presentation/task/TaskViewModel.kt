package com.example.studysmartandroidapp.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmartandroidapp.domain.model.Task
import com.example.studysmartandroidapp.domain.repository.SubjectRepository
import com.example.studysmartandroidapp.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository
): ViewModel() {
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
                return@launch
            }

            taskRepository.upsertTask(
                task = Task(
                    title = state.title,
                    description = state.description,
                    dueDate = state.dueDate
                        ?: LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                    relatedSubject = state.relatedToSubject,
                    priority = state.priority.value,
                    isComplete = state.isTaskComplete,
                    taskSubjectId = state.subjectId,
                    taskId = state.currentTaskId
                )
            )
        }
    }
}

