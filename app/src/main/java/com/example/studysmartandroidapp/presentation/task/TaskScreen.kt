package com.example.studysmartandroidapp.presentation.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.studysmartandroidapp.presentation.components.DeleteDialogue
import com.example.studysmartandroidapp.presentation.components.TaskCheckBox
import com.example.studysmartandroidapp.presentation.domain.model.Task
import com.example.studysmartandroidapp.presentation.theme.Red

@Composable
fun TaskScreen(
    task: Task
){

    var title by remember{ mutableStateOf("") }
    var description by remember{ mutableStateOf("") }

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

            Spacer(modifier = Modifier.height(10.dp))
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