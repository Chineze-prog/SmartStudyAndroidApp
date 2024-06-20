package com.example.studysmartandroidapp.presentation.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.studysmartandroidapp.presentation.components.DeleteDialogue
import com.example.studysmartandroidapp.presentation.components.ProjectDatePicker
import com.example.studysmartandroidapp.presentation.components.SubjectListBottomSheet
import com.example.studysmartandroidapp.presentation.components.TaskCheckBox
import com.example.studysmartandroidapp.presentation.subject.navigateToSubject
import com.example.studysmartandroidapp.utils.Priority
import com.example.studysmartandroidapp.utils.changeMillisToDateString
import com.example.studysmartandroidapp.utils.toLocalDate
import com.example.studysmartandroidapp.utils.SnackbarEvent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun TaskScreenRoute(navController: NavController, subjectId: Int?){
    val viewModel: TaskViewModel = hiltViewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()

    TaskScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarEvent = viewModel.snackbarEventFlow,
        onBackClick = {
            if (subjectId != null) {
                navController.navigateToSubject(subjectId)
            } else{
                navController.navigate("dashboard")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreen(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onBackClick: () -> Unit
){
    val currentDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant()
        .toEpochMilli().toLocalDate()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis =
        if(state.currentTaskId == null || state.dueDate == null ||
            state.dueDate.toLocalDate() < currentDate){
            LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
        else{ state.dueDate }
    )

    var isDatePickerDialogueOpen by remember{ mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember{ mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var isDeleteTaskDialogueOpen by remember{ mutableStateOf(false) }

    var taskTitleError by rememberSaveable { mutableStateOf <String?> (null) }

    taskTitleError = when{
        state.title.isBlank() -> "Please enter task title."
        state.title.length < 4 -> "Task title is too short."
        state.title.length > 20 -> "Task title is too long."
        else -> null
    }

    //snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest { event ->
            when(event) {
                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }

                SnackbarEvent.NavigateUp -> { onBackClick() }
            }
        }
    }

    DeleteDialogue(
        isOpen = isDeleteTaskDialogueOpen,
        title = "Delete Task?",
        bodyText = "Are you sure you want to delete this task?\nTHIS ACTION CANNOT BE UNDONE.",
        onDismissRequest = { isDeleteTaskDialogueOpen = false },
        onConfirmButtonClick = {
            onEvent(TaskEvent.DeleteTask)
            isDeleteTaskDialogueOpen = false
        }
    )

    ProjectDatePicker(
        state = datePickerState,
        isOpen = isDatePickerDialogueOpen,
        onDismissRequest = { isDatePickerDialogueOpen = false },
        onConfirmButtonClick = {
            onEvent(TaskEvent.OnDateChange(millis = datePickerState.selectedDateMillis))
            isDatePickerDialogueOpen = false
        }
    )

    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = state.subjects,
        //how to dismiss a bottom sheet if not through the onDismissRequest
        onSubjectClick = { relatedSubject ->
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if(!sheetState.isVisible) isBottomSheetOpen = false
            }

            onEvent(TaskEvent.OnRelatedSubjectSelect(relatedSubject))
        },
        onDismissRequest = { isBottomSheetOpen = false}
    )

    Scaffold (
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TaskScreenTopBar(
                taskExists = state.currentTaskId != null,
                isComplete = state.isTaskComplete,
                taskName = state.title,
                checkBoxBorderColor = state.priority.color,
                onBackButtonClick = onBackClick,
                onDeleteButtonClick = { isDeleteTaskDialogueOpen = true },
                onCheckBoxClick = { onEvent(TaskEvent.OnIsCompleteChange) }
            )
        }
    ){ paddingValue ->
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .fillMaxSize()
                .padding(paddingValue)
                .padding(horizontal = 12.dp)
        ){
            OutlinedTextField(
                value = state.title,
                onValueChange = { newTitle -> onEvent(TaskEvent.OnTitleChange(newTitle)) },
                label = { Text(text = "Title") },
                singleLine = true,
                isError = taskTitleError != null && state.title.isNotBlank(),
                supportingText = { Text(text = taskTitleError.orEmpty()) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = { newDesc -> onEvent(TaskEvent.OnTitleChange(newDesc)) },
                label = { Text(text = "Description") },
                //singleLine = true, commented so the text field can have multiple lines
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Due Date",
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = state.dueDate.changeMillisToDateString(),
                    style = MaterialTheme.typography.bodyLarge
                )

                IconButton(onClick = { isDatePickerDialogueOpen = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Due Date"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Priority",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()){
                Priority.entries.forEach { priority ->
                    PriorityButton(
                        modifier = Modifier.weight(1f), //so the three buttons are the same size
                        label = priority.title,
                        backgroundColor = priority.color,
                        //will change once view model is implemented
                        borderColor = if (priority == state.priority) { Color.White }
                        else Color.Transparent,
                        //will change once view model is implemented
                        labelColor = if (priority == state.priority) { Color.White }
                        else Color.White.copy(alpha = 0.7f),
                        onPriorityButtonClick = { onEvent(TaskEvent.OnPriorityChange(priority)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Related to subject",
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = state.relatedToSubject ?: "",
                    style = MaterialTheme.typography.bodyLarge
                )

                IconButton(onClick = { isBottomSheetOpen = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select related subject"
                    )
                }
            }

            Button(
                enabled = taskTitleError == null,
                onClick = { onEvent(TaskEvent.SaveTask) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ){
                Text(text = "Save")
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TaskScreenTopBar(
    taskExists: Boolean,
    isComplete: Boolean,
    taskName: String,
    checkBoxBorderColor: Color,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onCheckBoxClick: () -> Unit
){
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate Back to Subject"
                )
            }
        },
        title = {
            Text(
                text = taskName,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        actions = {
            //if the tasks already exists display its check box color
            if(taskExists){
                TaskCheckBox(
                    isComplete = isComplete,
                    borderColor = checkBoxBorderColor,
                    onCheckBoxClick = onCheckBoxClick
                )
            }

            IconButton(onClick = onDeleteButtonClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Subject"
                )
            }
        }
    )
}

@Composable
private fun PriorityButton(
    modifier: Modifier = Modifier,
    label: String,
    backgroundColor: Color,
    borderColor: Color,
    labelColor: Color,
    onPriorityButtonClick: () -> Unit
){
    Box(
        modifier = modifier
            .background(backgroundColor)
            .clickable { onPriorityButtonClick() }
            .padding(5.dp) //padding b/w the background and the border
            .border(1.dp, borderColor, RoundedCornerShape(5.dp))
            .padding(5.dp), //padding between the border and the label
        contentAlignment = Alignment.Center
    ){
        Text(text = label, color = labelColor)
    }
}