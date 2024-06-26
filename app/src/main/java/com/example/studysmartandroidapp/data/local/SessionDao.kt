package com.example.studysmartandroidapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.studysmartandroidapp.domain.model.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert suspend fun insertSession(session: Session)

    @Delete suspend fun deleteSession(session: Session)

    @Query("SELECT * FROM SESSION") fun getAllSessions(): Flow<List<Session>>

    @Query("SELECT * FROM SESSION WHERE sessionSubjectId = :subjectId")
    fun getRecentSessionsForSubject(subjectId: Int): Flow<List<Session>>

    // not sure why this was giving me an error but by changing it, it works now
    @Query("SELECT COALESCE(SUM(duration), 0) FROM SESSION")
    fun getTotalSessionsDuration(): Flow<Long>

    @Query("SELECT COALESCE(SUM(duration), 0) FROM SESSION WHERE sessionSubjectId = :subjectId")
    fun getTotalSessionsDurationBySubject(subjectId: Int): Flow<Long>

    @Query("DELETE FROM SESSION WHERE sessionSubjectId = :subjectId")
    fun deleteSessionsBySubjectId(subjectId: Int)
}
