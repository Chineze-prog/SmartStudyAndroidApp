package com.example.studysmartandroidapp.presentation.session

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.studysmartandroidapp.presentation.components.DeleteDialogue
import com.example.studysmartandroidapp.presentation.components.SubjectListBottomSheet
import com.example.studysmartandroidapp.presentation.components.studySessionsList
import com.example.studysmartandroidapp.utils.Constants.ACTION_SERVICE_CANCEL
import com.example.studysmartandroidapp.utils.Constants.ACTION_SERVICE_START
import com.example.studysmartandroidapp.utils.Constants.ACTION_SERVICE_STOP
import com.example.studysmartandroidapp.utils.SnackbarEvent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit

@Composable
fun SessionScreenRoute(navController: NavController, timerService: StudySessionTimerService) {
    val viewModel: SessionViewModel = hiltViewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()

    SessionScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarEvent = viewModel.snackbarEventFlow,
        onBackButtonClick = { navController.navigate("dashboard") },
        timerService = timerService
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SessionScreen(
    state: SessionState,
    onEvent: (SessionEvent) -> Unit,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onBackButtonClick: () -> Unit,
    timerService: StudySessionTimerService
) {
    val hours by timerService.hours
    val minutes by timerService.minutes
    val seconds by timerService.seconds
    val currentTimerState by timerService.currentTimerState

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState()

    var isBottomSheetOpen by remember { mutableStateOf(false) }

    var isDeleteSessionDialogueOpen by remember { mutableStateOf(false) }

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

    // whenever the state.subjects is changed the launched effect will be called
    LaunchedEffect(key1 = state.subjects) {

    }
    
    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = state.subjects,
        // how to dismiss a bottom sheet if not through the onDismissRequest
        onSubjectClick = { subject ->
            scope
                .launch { sheetState.hide() }
                .invokeOnCompletion { if (!sheetState.isVisible) isBottomSheetOpen = false }

            onEvent(SessionEvent.OnRelatedSubjectChange(subject))
        },
        onDismissRequest = { isBottomSheetOpen = false }
    )

    DeleteDialogue(
        isOpen = isDeleteSessionDialogueOpen,
        title = "Delete Session?",
        bodyText = "Are you sure you want to delete this session?\nTHIS ACTION CANNOT BE UNDONE.",
        onDismissRequest = { isDeleteSessionDialogueOpen = false },
        onConfirmButtonClick = {
            onEvent(SessionEvent.DeleteSession)
            isDeleteSessionDialogueOpen = false
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { SessionScreenTopBar(onBackButtonClick = onBackButtonClick) }) { paddingValue
        ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValue)) {
            // timer
            item {
                TimerSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    // makes the height of the timer section to be whatever is the
                    // width of that section, so the ratio is 1:1
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds
                )
            }

            item {
                RelatedSubjects(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    relatedSubject = state.relatedSubject ?: "",
                    selectSubjectButtonClick = { isBottomSheetOpen = true },
                    seconds = seconds
                )
            }

            item {
                ButtonsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    onStartButtonClick = {
                        if(state.subjectId != null && state.relatedSubject != null) {
                            ServiceHelper.triggerForegroundService(
                                context = context,
                                action =
                                if (currentTimerState == TimerState.STARTED) {
                                    ACTION_SERVICE_STOP
                                } else ACTION_SERVICE_START
                            )
                            // to save the subjectId so that if the session screen is restarted 
                            // while the  session is ongoing that info isn't lost
                            timerService.subjectId.value = state.subjectId
                        }
                        else{
                            onEvent(SessionEvent.NotifyToUpdateSubject)
                        }
                    },
                    onCancelButtonClick = {
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = ACTION_SERVICE_CANCEL
                        )
                    },
                    onFinishButtonClick = {
                        val duration = timerService.duration.toLong(DurationUnit.SECONDS)

                        //only be triggered if the duration is more than 36 secs
                        if(duration >= 36) {
                            ServiceHelper.triggerForegroundService(
                                context = context,
                                action = ACTION_SERVICE_CANCEL
                            )
                        }
                        onEvent(SessionEvent.SaveSession(duration))
                    },
                    timerState = currentTimerState,
                    seconds = seconds
                )
            }

            studySessionsList(
                sectionTitle = "STUDY SESSIONS HISTORY",
                sessions = state.sessions,
                emptyListText =
                    "You don't have any recent study sessions.\n Start a study " +
                        "session to begin recording your progress.\n",
                onDeleteIconClick = { session ->
                    onEvent(SessionEvent.OnDeleteSessionButtonClick(session))
                    isDeleteSessionDialogueOpen = true
                }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SessionScreenTopBar(onBackButtonClick: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Navigate Back")
            }
        },
        title = { Text(text = "Study Session", style = MaterialTheme.typography.headlineSmall) }
    )
}

@Composable
private fun TimerSection(
    modifier: Modifier,
    hours: String,
    minutes: String,
    seconds: String,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier =
            Modifier
                .size(250.dp)
                .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        )
        Row {
            AnimatedContent(
                targetState = hours,
                label = hours,
                transitionSpec = { timerTextAnimation() }
            ) { hours ->
                Text(
                    text = "$hours:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }

            AnimatedContent(
                targetState = minutes,
                label = minutes,
                transitionSpec = { timerTextAnimation() }
            ) { minutes ->
                Text(
                    text = "$minutes:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }

            AnimatedContent(
                targetState = seconds,
                label = seconds,
                transitionSpec = { timerTextAnimation() }
            ) { seconds ->
                Text(
                    text = seconds,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
        }
    }
}

// to make the timer animation smoother
private fun timerTextAnimation(duration: Int = 600): ContentTransform {
    return slideInVertically(animationSpec = tween(duration)) { fullHeight -> fullHeight } +
        fadeIn(animationSpec = tween(duration)) togetherWith
        slideOutVertically(animationSpec = tween(duration)) { fullHeight -> -fullHeight } +
            fadeOut(animationSpec = tween(duration))
}

@Composable
private fun RelatedSubjects(
    modifier: Modifier,
    relatedSubject: String,
    selectSubjectButtonClick: () -> Unit,
    seconds: String
) {
    Column(modifier = modifier) {
        Text(text = "Related to subject", style = MaterialTheme.typography.bodySmall)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = relatedSubject, style = MaterialTheme.typography.bodyLarge)

            IconButton(
                enabled = seconds == "00",
                onClick = selectSubjectButtonClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select related subject"
                )
            }
        }
    }
}

@Composable
private fun ButtonsSection(
    modifier: Modifier,
    onStartButtonClick: () -> Unit,
    onCancelButtonClick: () -> Unit,
    onFinishButtonClick: () -> Unit,
    timerState: TimerState,
    seconds: String
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Button(
            onClick = onCancelButtonClick,
            enabled = seconds != "00" && timerState != TimerState.STARTED
        ) {
            Text(modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp), text = "Cancel")
        }

        Button(
            onClick = onStartButtonClick,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        if (timerState == TimerState.STARTED) Color.Red
                        else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
                text =
                    when (timerState) {
                        TimerState.STARTED -> "Stop"
                        TimerState.STOPPED -> "Resume"
                        else -> "Start"
                    }
            )
        }

        Button(
            onClick = onFinishButtonClick,
            enabled = seconds != "00" && timerState != TimerState.STARTED
        ) {
            Text(modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp), text = "Finish")
        }
    }
}
