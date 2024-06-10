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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.example.studysmartandroidapp.presentation.components.DeleteDialogue
import com.example.studysmartandroidapp.presentation.components.ProjectDatePicker
import com.example.studysmartandroidapp.presentation.components.SubjectListBottomSheet
import com.example.studysmartandroidapp.presentation.components.TaskCheckBox
import com.example.studysmartandroidapp.presentation.domain.model.Task
import com.example.studysmartandroidapp.presentation.theme.Red
import com.example.studysmartandroidapp.presentation.utils.Priority
import com.example.studysmartandroidapp.presentation.utils.changeMillisToDateString
import com.example.studysmartandroidapp.subjects
import kotlinx.coroutines.launch
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    task: Task
){

    var relatedSubject by remember { mutableStateOf("") }
    var title by remember{ mutableStateOf("") }
    var description by remember{ mutableStateOf("") }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var isDatePickerDialogueOpen by remember{ mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember{ mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var isDeleteTaskDialogueOpen by remember{ mutableStateOf(false) }

    var taskTitleError by rememberSaveable { mutableStateOf <String?> (null) }

    taskTitleError = when{
        title.isBlank() -> "Please enter task title."
        title.length < 4 -> "Task title is too short."
        title.length > 20 -> "Task title is too long."
        else -> null
    }

    DeleteDialogue(
        isOpen = isDeleteTaskDialogueOpen,
        title = "Delete Task?",
        bodyText = "Are you sure you want to delete this task?\nTHIS ACTION CANNOT BE UNDONE.",
        onDismissRequest = { isDeleteTaskDialogueOpen = false },
        onConfirmButtonClick = { isDeleteTaskDialogueOpen = false }
    )

    ProjectDatePicker(
        state = datePickerState,
        isOpen = isDatePickerDialogueOpen,
        onDismissRequest = { isDatePickerDialogueOpen = false },
        onConfirmButtonClick = { isDatePickerDialogueOpen = false  }
    )

    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = subjects,
        //how to dismiss a bottom sheet if not through the onDismissRequest
        onSubjectClick = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if(!sheetState.isVisible) isBottomSheetOpen = false
            }
            relatedSubject = it.subjectName
        },
        onDismissRequest = { isBottomSheetOpen = false}
    )

    Scaffold (
        topBar = {
            TaskScreenTopBar(
                taskExists = true,
                isComplete = false,//task.isComplete,
                taskName = "Task",//task.title,
                checkBoxBorderColor = Red,
                onBackButtonClick = { /*TODO*/ },
                onDeleteButtonClick = { isDeleteTaskDialogueOpen = true },
                onCheckBoxClick = { /*TODO*/ }
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
                value = title,
                onValueChange = {newTitle -> title = newTitle},
                label = { Text(text = "Title") },
                singleLine = true,
                isError = taskTitleError != null && title.isNotBlank(),
                supportingText = { Text(text = taskTitleError.orEmpty()) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = {newDescription -> description = newDescription},
                label = { Text(text = "Description") },
                //singleLine = true, so the text field can have multiple lines
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
                    text = datePickerState.selectedDateMillis.changeMillisToDateString(),
                    style = MaterialTheme.typography.bodyLarge
                )

                IconButton(onClick = { isDatePickerDialogueOpen = true } ) {
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
                        borderColor = if (priority == Priority.MEDIUM) { Color.White }
                        else Color.Transparent,
                        //will change once view model is implemented
                        labelColor = if (priority == Priority.MEDIUM) { Color.White }
                        else Color.White.copy(alpha = 0.7f),
                        onPriorityButtonClick = { /*TODO*/ }
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
                    text = relatedSubject,
                    style = MaterialTheme.typography.bodyLarge
                )

                IconButton(onClick = { isBottomSheetOpen = true } ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select related subject"
                    )
                }
            }
            Button(
                enabled = taskTitleError == null,
                onClick = { /*TODO*/ },
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
            IconButton(onClick = { onBackButtonClick }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate Back to Subject")
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

            IconButton(onClick = onDeleteButtonClick ) {
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