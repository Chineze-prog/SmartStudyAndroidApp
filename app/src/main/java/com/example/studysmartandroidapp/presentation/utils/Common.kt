package com.example.studysmartandroidapp.presentation.utils

import androidx.compose.ui.graphics.Color
import com.example.studysmartandroidapp.presentation.theme.Green
import com.example.studysmartandroidapp.presentation.theme.Orange
import com.example.studysmartandroidapp.presentation.theme.Red
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

enum class Priority(val title: String, val color: Color, val value: Int){
    LOW(title = "Low", color = Green, value = 0),
    MEDIUM(title = "Medium", color = Orange, value = 1),
    HIGH(title = "High", color = Red, value = 2);

    companion object{
        fun fromInt(value: Int) = entries.firstOrNull{it.value == value} ?: MEDIUM
    }
}

fun Long?.changeMillisToDateString(): String{
    val date: LocalDate = this?.let{ millis ->
        Instant
            .ofEpochMilli(millis)
            .atZone(ZoneOffset.UTC)//ZoneId.systemDefault())
            .toLocalDate()
    } ?: LocalDate.now()

    return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
}

fun Long.toLocalDate(): LocalDate{
    return Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
}