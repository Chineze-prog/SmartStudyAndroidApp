package com.example.studysmartandroidapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import com.example.studysmartandroidapp.domain.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// ViewModel is a part of the android architecture components and serves as a bridge b/w the
// repository and the UI layer
// It is responsible for managing UI related data and its state
// It observes changes in the data from the repository and provided the data to the UI

@HiltViewModel
// puts a spacial mark on a class that helps dagger Hilt understand that this class
// is a ViewModel
class DashboardViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository
): ViewModel() {
}