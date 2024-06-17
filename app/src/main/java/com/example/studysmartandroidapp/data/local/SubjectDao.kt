package com.example.studysmartandroidapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.studysmartandroidapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow

/*
suspend VS flow<> - is the function a continuous flow of data or one time event
.e.g. in upsertSubject, when we insert the subject it is a one time event (when the save button is
pressed in the UI the subject is saved or updated in the subject entity once {suspend}
AND with getTotalSubjectCount it is a continuous check/flow of data since there is no button to press when you
want to check the current number of subjects but we need to continuously display the number of
subjects which can change at any time and should be updated instantly without user interaction {Flow}
 */

@Dao
interface SubjectDao {
    //either insert the subject if the subject doesn't exist or update it if it does
    @Upsert
    suspend fun upsertSubject(subject: Subject)

    @Query("SELECT COUNT(*) FROM SUBJECT")
    fun getTotalSubjectCount(): Flow<Int>

    @Query("SELECT SUM(goalStudyHours) FROM SUBJECT")
    fun getTotalGoalHours(): Flow<Float>

    @Query("SELECT * FROM SUBJECT WHERE subjectId = :subjectId")
    suspend fun getSubjectById(subjectId: Int): Subject?

    @Query("DELETE FROM SUBJECT WHERE subjectId = :subjectId")
    suspend fun deleteSubject(subjectId: Int)

    @Query("SELECT * FROM SUBJECT")
    fun getAllSubjects(): Flow<List<Subject>>
}