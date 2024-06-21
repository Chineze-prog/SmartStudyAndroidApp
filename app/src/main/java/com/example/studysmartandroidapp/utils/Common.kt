package com.example.studysmartandroidapp.utils

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import com.example.studysmartandroidapp.presentation.theme.Green
import com.example.studysmartandroidapp.presentation.theme.Orange
import com.example.studysmartandroidapp.presentation.theme.Red
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

enum class Priority(val title: String, val color: Color, val value: Int) {
    LOW(title = "Low", color = Green, value = 0),
    MEDIUM(title = "Medium", color = Orange, value = 1),
    HIGH(title = "High", color = Red, value = 2);

    companion object {
        fun fromInt(value: Int) = entries.firstOrNull { it.value == value } ?: MEDIUM
    }
}

fun Long?.changeMillisToDateString(): String {
    val date: LocalDate =
        this?.let { millis -> Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate() }
            ?: LocalDate.now()

    return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
}

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
}

fun Long.toHours(): Float {
    val hours = this.toFloat() / 3600f
    return "%.2f".format(hours).toFloat()
}

/*
   A sealed class in Kotlin is a class that is marked with the sealed keyword. It is used to define
   a closed set of subclasses. A sealed class is a way to define a restricted class hierarchy where
   subclasses are predefined and finite. The subclasses of a sealed class are defined within the
   sealed class itself, and each subclass must be declared as inner or data or class, with no other
   modifiers allowed.
*/
sealed class SnackbarEvent {
    data class ShowSnackbar(
        val message: String,
        val duration: SnackbarDuration = SnackbarDuration.Short
    ) : SnackbarEvent()

    data object NavigateUp : SnackbarEvent()
}

// padStart - if the string is not of 2 length, it will append the zero char. for values less than
// 10 .e.g. 9 will be 09
fun Int.pad(): String {
    return this.toString().padStart(length = 2, padChar = '0')
}
