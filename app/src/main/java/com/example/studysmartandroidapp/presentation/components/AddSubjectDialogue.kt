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
                                    .border(width = 1.dp, color =  if (color == selectedColors)
                                        Color.Black else Color.Transparent)
                                    .background(brush = Brush.horizontalGradient(color))
                            )
                        }
                    }

                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = onSubjectNameChange,
                        label = { Text(text = "Subject Name")},
                        singleLine = true,
                        supportingText = { Text(text = "Please enter subject name")}
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = goalStudyHours,
                        onValueChange = onGoalStudyHoursChange,
                        label = { Text(text = "Goal Study Hours")},
                        singleLine = true,
                        supportingText = { Text(text = "Please enter goal study hours")},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
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