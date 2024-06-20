package com.example.studysmartandroidapp.data.repository

import com.example.studysmartandroidapp.data.local.SessionDao
import com.example.studysmartandroidapp.data.local.SubjectDao
import com.example.studysmartandroidapp.data.local.TaskDao
import com.example.studysmartandroidapp.domain.model.Subject
import com.example.studysmartandroidapp.domain.repository.SubjectRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class SubjectRepositoryImpl
@Inject
constructor(
    private val subjectDao: SubjectDao,
    private val taskDao: TaskDao,
    private val sessionDao: SessionDao
) : SubjectRepository {
    override suspend fun upsertSubject(subject: Subject) {
        subjectDao.upsertSubject(subject)
    }

    override fun getTotalSubjectCount(): Flow<Int> {
        return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHours(): Flow<Float> {
        return subjectDao.getTotalGoalHours()
    }

    override suspend fun getSubjectById(subjectId: Int): Subject? {
        return subjectDao.getSubjectById(subjectId)
    }

    override suspend fun deleteSubject(subjectId: Int) {
        taskDao.deleteTaskBySubjectId(subjectId)
        sessionDao.deleteSessionsBySubjectId(subjectId)
        subjectDao.deleteSubject(subjectId)
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects()
    }
}
