package com.example.studysmartandroidapp.presentation.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.studysmartandroidapp.domain.model.Task
import com.example.studysmartandroidapp.presentation.components.AddSubjectDialogue
import com.example.studysmartandroidapp.presentation.components.CountCard
import com.example.studysmartandroidapp.presentation.components.DeleteDialogue
import com.example.studysmartandroidapp.presentation.components.studySessionsList
import com.example.studysmartandroidapp.presentation.components.tasksList
import com.example.studysmartandroidapp.presentation.task.navigateToTask
import com.example.studysmartandroidapp.utils.SnackbarEvent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

// states - are the values that can change
@Composable
fun SubjectScreenRoute(navController: NavController) {

    val viewModel: SubjectViewModel = hiltViewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()

    val overdueTasks by viewModel.overdueTasks.collectAsStateWithLifecycle()

    if (state.currentSubjectId != null) {
        SubjectScreen(
            state = state,
            onEvent = viewModel::onEvent,
            overdueTasks = overdueTasks,
            snackbarEvent = viewModel.snackbarEventFlow,
            onTaskCardClick = { taskId ->
                navController.navigateToTask(taskId = taskId, subjectId = state.currentSubjectId)
            },
            onAddTaskClick = {
                navController.navigateToTask(taskId = null, subjectId = state.currentSubjectId)
            },
            onBackButtonClick = { navController.navigate("dashboard") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreen(
    state: SubjectState,
    onEvent: (SubjectEvent) -> Unit,
    overdueTasks: List<Task>,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onTaskCardClick: (Int?) -> Unit,
    onAddTaskClick: () -> Unit,
    onBackButtonClick: () -> Unit
) {
    // remembers the state of the lazy list
    val listState = rememberLazyListState()

    // the add Task button should only be expanded when the list is on the 1st item
    val isExpanded by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var isEditSubjectDialogueOpen by rememberSaveable { mutableStateOf(false) }

    var isDeleteSubjectDialogueOpen by rememberSaveable { mutableStateOf(false) }

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
                SnackbarEvent.NavigateUp -> {
                    onBackButtonClick()
                }
            }
        }
    }

    // since there is no button to update the progress, we use launched effect
    // and changes in the key determine how frequent the launch will be called
    LaunchedEffect(key1 = state.studiedHours, key2 = state.goalStudyHours) {
        onEvent(SubjectEvent.UpdateProgress)
    }

    // for editing the subject
    AddSubjectDialogue(
        isOpen = isEditSubjectDialogueOpen,
        subjectName = state.subjectName,
        goalStudyHours = state.goalStudyHours,
        selectedColors = state.subjectCardColors,
        onDismissRequest = { isEditSubjectDialogueOpen = false },
        onConfirmButtonClick = {
            onEvent(SubjectEvent.UpdateSubject)
            isEditSubjectDialogueOpen = false
        },
        onColorChange = { newColor -> onEvent(SubjectEvent.OnSubjectCardColorChange(newColor)) },
        onSubjectNameChange = { newName -> onEvent(SubjectEvent.OnSubjectNameChange(newName)) },
        onGoalStudyHoursChange = { newGsh -> onEvent(SubjectEvent.OnGoalStudyHoursChange(newGsh)) }
    )

    DeleteDialogue(
        isOpen = isDeleteSubjectDialogueOpen,
        title = "Delete Subject?",
        bodyText =
            "Are you sure you want to delete this subject?\nAll related tasks and study " +
                "sessions will be permanently removed.\nTHIS ACTION CANNOT BE UNDONE.",
        onDismissRequest = { isDeleteSubjectDialogueOpen = false },
        onConfirmButtonClick = {
            onEvent(SubjectEvent.DeleteSubject)
            isDeleteSubjectDialogueOpen = false
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
            isDeleteSessionDialogueOpen = false
            onEvent(SubjectEvent.DeleteSession)
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubjectScreenTopBar(
                subjectName = state.subjectName,
                onBackButtonClick = onBackButtonClick,
                onDeleteButtonClick = { isDeleteSubjectDialogueOpen = true },
                onEditButtonClick = { isEditSubjectDialogueOpen = true },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTaskClick,
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task") },
                text = { Text(text = "Add Task") },
                expanded = isExpanded
            )
        }
    ) { paddingValue ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValue), state = listState) {
            item {
                SubjectOverviewSection(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    goalStudyHours = state.goalStudyHours,
                    studiedHours = state.studiedHours.toString(),
                    progress = state.progress
                )
            }

            tasksList(
                sectionTitle = "UPCOMING TASKS",
                tasks = state.upcomingTasks,
                emptyListText =
                    "You don't have any upcoming tasks.\n Click the + button in the" +
                        " subject screen to add a new task.",
                onTaskCardClick = onTaskCardClick,
                onCheckBoxClick = { newStatus ->
                    onEvent(SubjectEvent.OnTaskIsCompleteChange(newStatus))
                }
            )

            item { Spacer(modifier = Modifier.height(20.dp)) }

            tasksList(
                sectionTitle = "OVERDUE TASKS",
                tasks = overdueTasks,
                emptyListText = "You currently do not have any overdue tasks.",
                onTaskCardClick = onTaskCardClick,
                onCheckBoxClick = { newStatus ->
                    onEvent(SubjectEvent.OnTaskIsCompleteChange(newStatus))
                }
            )

            item { Spacer(modifier = Modifier.height(20.dp)) }

            tasksList(
                sectionTitle = "COMPLETED TASKS",
                tasks = state.completedTasks,
                emptyListText =
                    "You don't have any completed tasks.\n Click the task's check box" +
                        " upon completion.",
                onTaskCardClick = onTaskCardClick,
                onCheckBoxClick = { newStatus ->
                    onEvent(SubjectEvent.OnTaskIsCompleteChange(newStatus))
                }
            )

            item { Spacer(modifier = Modifier.height(20.dp)) }

            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                sessions = state.recentSessions,
                emptyListText =
                    "You don't have any recent study sessions.\n Start a study " +
                        "session to begin recording your progress.\n",
                onDeleteIconClick = { session ->
                    isDeleteSessionDialogueOpen = true
                    onEvent(SubjectEvent.OnDeleteSessionButtonClick(session))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreenTopBar(
    subjectName: String,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onEditButtonClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate back to dashboard"
                )
            }
        },
        title = {
            Text(
                text = subjectName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineMedium
            )
        },
        actions = {
            IconButton(onClick = onDeleteButtonClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Subject")
            }

            IconButton(onClick = onEditButtonClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Subject")
            }
        }
    )
}

@Composable
private fun SubjectOverviewSection(
    modifier: Modifier,
    goalStudyHours: String,
    studiedHours: String,
    progress: Float
) {
    // converting the progress to %
    // uses remember so the value changes when progress changes
    // uses coerceIn to make sure the value remains within range
    val percentageProgress = remember(progress) { (progress * 100).toInt().coerceIn(0, 100) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count = goalStudyHours
        )

        Spacer(modifier = Modifier.width(10.dp))

        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Studied Hours",
            count = studiedHours
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(modifier = Modifier.size(75.dp), contentAlignment = Alignment.Center) {
            // constant progress bar, remains the same(background)
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = 1f,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            // progress bar that moves (tracks actual progress)
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = progress,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round
            )

            Text(text = "${percentageProgress}%")
        }
    }
}
