package com.example.studysmartandroidapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.studysmartandroidapp.presentation.theme.gradient1
import com.example.studysmartandroidapp.presentation.theme.gradient2
import com.example.studysmartandroidapp.presentation.theme.gradient3
import com.example.studysmartandroidapp.presentation.theme.gradient4
import com.example.studysmartandroidapp.presentation.theme.gradient5

@Entity
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val subjectId: Int? = null,
    val subjectName: String,
    val goalStudyHours: Float,
    val colors: List<Int>
){
    companion object{
        val subjectCardColors = listOf(gradient1, gradient2, gradient3, gradient4, gradient5)
    }
}
