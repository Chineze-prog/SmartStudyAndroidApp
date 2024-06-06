package com.example.studysmartandroidapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.studysmartandroidapp.presentation.domain.model.Subject

@Composable
fun AddSubjectDialogue(
    isOpen: Boolean,
    title: String = "Add/Update Subject",
    subjectName: String,
    goalStudyHours: String,
    selectedColors: List<Color>,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    onColorChange: (List<Color>) -> Unit,
    onSubjectNameChange: (String) -> Unit,
    onGoalStudyHoursChange: (String) -> Unit
){
    var subjectNameError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    subjectNameError = when{
        subjectName.isBlank() -> "Please enter subject name."
        subjectName.length < 2 -> "Subject Name is too short."
        subjectName.length > 20 -> "Subject Name is too long."
        else -> null
    }

    var goalStudyHoursError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    goalStudyHoursError = when{
        goalStudyHours.isBlank() -> "Please enter goal study hours."
        goalStudyHours.toFloatOrNull() == null -> "Invalid number."
        goalStudyHours.toFloat() < 1f -> "Please set to at least 1 hour."
        goalStudyHours.toFloat() > 1000f -> "Please set a maximum of 1000 hours."
        else -> null
    }

    if(isOpen) {
        AlertDialog(
            title = { Text(text = title) },
            text = {
                Column{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Subject.subjectCardColors.forEach{ color ->
                            Box(
                                modifier = Modifier
                                    .clickable { onColorChange(color) }
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        color = if (color == selectedColors) Color.Black
                                        else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .background(brush = Brush.horizontalGradient(color))
                            )
                        }
                    }

                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = onSubjectNameChange,
                        label = { Text(text = "Subject Name")},
                        singleLine = true,
                        isError = subjectNameError != null && subjectName.isNotBlank(),
                        supportingText = { Text(text = subjectNameError.orEmpty())},
                        )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = goalStudyHours,
                        onValueChange = onGoalStudyHoursChange,
                        label = { Text(text = "Goal Study Hours")},
                        singleLine = true,
                        isError = goalStudyHoursError != null && goalStudyHours.isNotBlank(),
                        supportingText = { Text(text = goalStudyHoursError.orEmpty())},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = onConfirmButtonClick,
                    enabled = subjectNameError == null && goalStudyHoursError == null
                ) {
                    Text(text = "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}