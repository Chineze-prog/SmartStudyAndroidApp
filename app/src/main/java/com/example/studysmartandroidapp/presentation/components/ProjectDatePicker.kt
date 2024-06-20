package com.example.studysmartandroidapp.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.studysmartandroidapp.utils.toLocalDate
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProjectDatePicker(
    state: DatePickerState,
    isOpen: Boolean,
    dismissButtonText: String = "Cancel",
    confirmButtonText: String = "Ok",
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit
) {
    if (isOpen) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) { Text(text = confirmButtonText) }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) { Text(text = dismissButtonText) }
            },
            content = {
                DatePicker(
                    state = state,
                    dateValidator = { timeStamp ->
                        val selectedDate =
                            Instant.ofEpochMilli(timeStamp).atZone(ZoneOffset.UTC).toLocalDate()

                        val currentDate =
                            LocalDate.now()
                                .atStartOfDay(ZoneOffset.UTC)
                                .toInstant()
                                .toEpochMilli()
                                .toLocalDate()

                        selectedDate >= currentDate
                    }
                )
            }
        )
    }
}
