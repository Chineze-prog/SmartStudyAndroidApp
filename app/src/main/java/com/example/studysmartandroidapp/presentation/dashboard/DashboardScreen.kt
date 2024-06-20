package com.example.studysmartandroidapp.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.studysmartandroidapp.R
import com.example.studysmartandroidapp.domain.model.Session
import com.example.studysmartandroidapp.domain.model.Subject
import com.example.studysmartandroidapp.domain.model.Task
import com.example.studysmartandroidapp.presentation.components.AddSubjectDialogue
import com.example.studysmartandroidapp.presentation.components.CountCard
import com.example.studysmartandroidapp.presentation.components.DeleteDialogue
import com.example.studysmartandroidapp.presentation.components.SubjectCard
import com.example.studysmartandroidapp.presentation.components.studySessionsList
import com.example.studysmartandroidapp.presentation.components.tasksList
import com.example.studysmartandroidapp.presentation.subject.navigateToSubject
import com.example.studysmartandroidapp.presentation.task.navigateToTask
import com.example.studysmartandroidapp.utils.SnackbarEvent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DashboardScreenRoute(navController: NavController) {
    val viewModel: DashboardViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val recentSessions by viewModel.recentSessions.collectAsStateWithLifecycle()

    DashboardScreen(
        state = state,
        tasks = tasks,
        recentSessions = recentSessions,
        onEvent = viewModel::onEvent,
        snackbarEvent = viewModel.snackbarEventFlow,
        onStartSessionButtonClick = { navController.navigate("session") },
        onSubjectCardClick = { subjectId ->
            if (subjectId != null) {
                navController.navigateToSubject(subjectId)
            }
        },
        onTaskCardClick = { navController.navigateToTask(taskId = it, subjectId = null) }
    )
}

@Composable
private fun DashboardScreen(
    state: DashboardState,
    tasks: List<Task>,
    recentSessions: List<Session>,
    onEvent: (DashboardEvent) -> Unit,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onStartSessionButtonClick: () -> Unit,
    onSubjectCardClick: (Int?) -> Unit,
    onTaskCardClick: (Int?) -> Unit
) {
    var isAddSubjectDialogueOpen by rememberSaveable { mutableStateOf(false) }

    var isDeleteSessionDialogueOpen by rememberSaveable { mutableStateOf(false) }

    // snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest { event ->
            when (event) {
                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }
                SnackbarEvent.NavigateUp -> {}
            }
        }
    }

    AddSubjectDialogue(
        isOpen = isAddSubjectDialogueOpen,
        subjectName = state.subjectName,
        goalStudyHours = state.goalStudyHours,
        selectedColors = state.subjectCardColors,
        onDismissRequest = { isAddSubjectDialogueOpen = false },
        onConfirmButtonClick = {
            onEvent(DashboardEvent.SaveSubject)
            isAddSubjectDialogueOpen = false
        },
        onColorChange = { newColor -> onEvent(DashboardEvent.OnSubjectCardColorChange(newColor)) },
        onSubjectNameChange = { newName -> onEvent(DashboardEvent.OnSubjectNameChange(newName)) },
        onGoalStudyHoursChange = { newGsh ->
            onEvent(DashboardEvent.OnGoalStudyHoursChange(newGsh))
        }
    )

    DeleteDialogue(
        isOpen = isDeleteSessionDialogueOpen,
        title = "Delete Session?",
        bodyText =
            "Are you sure you want to delete this session?\nYour studied hours will be " +
                "reduced by this session's time.\nTHIS ACTION CANNOT BE UNDONE.",
        onDismissRequest = { isDeleteSessionDialogueOpen = false },
        onConfirmButtonClick = {
            onEvent(DashboardEvent.DeleteSession)
            isDeleteSessionDialogueOpen = false
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { DashboardScreenTopBar() }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            item { // count cards
                CountCardsSection(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    subjectCount = state.totalSubjectCount,
                    studiedHours = state.totalStudiedHours.toString(),
                    goalStudyHours = state.totalGoalStudyHours.toString()
                )
            }

            item { // subject cards
                SubjectCardsSection(
                    modifier = Modifier.fillMaxWidth(),
                    subjectsList = state.subjects,
                    onAddIconClick = { isAddSubjectDialogueOpen = true },
                    onSubjectCardClick = onSubjectCardClick
                )
            }

            item { // start study session button
                Button(
                    modifier =
                        Modifier.fillMaxWidth().padding(horizontal = 48.dp, vertical = 20.dp),
                    onClick = onStartSessionButtonClick
                ) {
                    Text(text = "Start Study Session")
                }
            }

            tasksList(
                sectionTitle = "UPCOMING TASKS",
                tasks = tasks,
                emptyListText =
                    "You don't have any upcoming tasks.\n Click the + button in the" +
                        " subject screen to add a new task.",
                onTaskCardClick = onTaskCardClick,
                onCheckBoxClick = { newStatus ->
                    onEvent(DashboardEvent.OnTaskIsCompleteChange(newStatus))
                }
            )

            item { Spacer(modifier = Modifier.height(20.dp)) }

            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                sessions = recentSessions,
                emptyListText =
                    "You don't have any recent study sessions.\n Start a study " +
                        "session to begin recording your progress.\n",
                onDeleteIconClick = { session ->
                    onEvent(DashboardEvent.OnDeleteSessionButtonClick(session))
                    isDeleteSessionDialogueOpen = true
                }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DashboardScreenTopBar() {
    CenterAlignedTopAppBar(
        title = { Text(text = "Study Smart", style = MaterialTheme.typography.headlineMedium) }
    )
}

@Composable
private fun CountCardsSection(
    modifier: Modifier,
    subjectCount: Int,
    studiedHours: String,
    goalStudyHours: String
) {
    Row(modifier) {
        CountCard(
            modifier = Modifier.weight(1f), // so that all the cards will be equally sized
            headingText = "Subject Count",
            count = "$subjectCount"
        )

        Spacer(modifier = Modifier.width(10.dp))

        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Studied Hours",
            count = studiedHours
        )

        Spacer(modifier = Modifier.width(10.dp))

        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count = goalStudyHours
        )
    }
}

@Composable
private fun SubjectCardsSection(
    modifier: Modifier,
    subjectsList: List<Subject>,
    emptyListText: String =
        "You currently don't have any subjects.\n Click the + button to add " + "a new subject",
    onAddIconClick: () -> Unit,
    onSubjectCardClick: (Int?) -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SUBJECTS",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp)
            )

            IconButton(onClick = onAddIconClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Subject")
            }
        }

        if (subjectsList.isEmpty()) {
            Image(
                modifier = Modifier.size(120.dp).align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.img_books),
                contentDescription = emptyListText
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = emptyListText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp), // space between each
            contentPadding =
                PaddingValues(start = 12.dp, end = 12.dp) // space at the beginning and end
        ) {
            items(subjectsList) { subject ->
                SubjectCard(
                    subjectName = subject.subjectName,
                    gradientColors = subject.colors.map { intValue -> Color(intValue) },
                    onClick = { onSubjectCardClick(subject.subjectId) }
                )
            }
        }
    }
}
