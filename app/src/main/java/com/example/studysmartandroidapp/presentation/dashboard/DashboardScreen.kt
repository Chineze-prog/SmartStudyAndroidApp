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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.studysmartandroidapp.R
import com.example.studysmartandroidapp.presentation.components.AddSubjectDialogue
import com.example.studysmartandroidapp.presentation.components.CountCard
import com.example.studysmartandroidapp.presentation.components.DeleteDialogue
import com.example.studysmartandroidapp.presentation.components.SubjectCard
import com.example.studysmartandroidapp.presentation.components.studySessionsList
import com.example.studysmartandroidapp.presentation.components.tasksList
import com.example.studysmartandroidapp.presentation.domain.model.Session
import com.example.studysmartandroidapp.presentation.domain.model.Subject
import com.example.studysmartandroidapp.presentation.domain.model.Task

@Composable
fun DashboardScreen(){

    val subjects = listOf(
        Subject(subjectId = 0, subjectName = "English", goalStudyHours = 10f, colors = Subject.subjectCardColors[0]),
        Subject(subjectId = 0, subjectName = "Physics", goalStudyHours = 10f, colors = Subject.subjectCardColors[1]),
        Subject(subjectId = 0, subjectName = "Maths", goalStudyHours = 10f, colors = Subject.subjectCardColors[2]),
        Subject(subjectId = 0, subjectName = "Geology", goalStudyHours = 10f, colors = Subject.subjectCardColors[3]),
        Subject(subjectId = 0, subjectName = "Fine Arts", goalStudyHours = 10f, colors = Subject.subjectCardColors[4])
    )

    val tasks = listOf(
        Task(
            taskId = 1,
            taskSubjectId = 0,
            title = "Prepare notes",
            description = "",
            dueDate = 0L,
            priority = 0,
            relatedSubject = "",
            isComplete = false
        ),
        Task(
            taskId = 1,
            taskSubjectId = 0,
            title = "Do Homework",
            description = "",
            dueDate = 0L,
            priority = 1,
            relatedSubject = "",
            isComplete = true
        ),
        Task(
            taskId = 1,
            taskSubjectId = 0,
            title = "Go Cooking",
            description = "",
            dueDate = 0L,
            priority = 2,
            relatedSubject = "",
            isComplete = false
        ),
        Task(
            taskId = 1,
            taskSubjectId = 0,
            title = "Assignment",
            description = "",
            dueDate = 0L,
            priority = 1,
            relatedSubject = "",
            isComplete = false
        ),
        Task(
            taskId = 1,
            taskSubjectId = 0,
            title = "Write Poem",
            description = "",
            dueDate = 0L,
            priority = 0,
            relatedSubject = "",
            isComplete = true
        )
    )

    val sessions = listOf(
        Session(
            sessionId = 0,
            sessionSubjectId = 0,
            relatedSubject = "English",
            date = 0L,
            duration = 2
        ),
        Session(
            sessionId = 0,
            sessionSubjectId = 0,
            relatedSubject = "Maths",
            date = 0L,
            duration = 2
        ),
        Session(
            sessionId = 0,
            sessionSubjectId = 0,
            relatedSubject = "Physics",
            date = 0L,
            duration = 2
        ),
        Session(
            sessionId = 0,
            sessionSubjectId = 0,
            relatedSubject = "Psychology",
            date = 0L,
            duration = 2
        ),
        Session(
            sessionId = 0,
            sessionSubjectId = 0,
            relatedSubject = "Computer Science",
            date = 0L,
            duration = 2
        ),
        Session(
            sessionId = 0,
            sessionSubjectId = 0,
            relatedSubject = "Biology",
            date = 0L,
            duration = 2
        )
    )

    var isAddSubjectDialogueOpen by remember{
        mutableStateOf(false)
    }

    var isDeleteDialogueOpen by remember{
        mutableStateOf(false)
    }

    var subjectName by remember{
        mutableStateOf("")
    }

    var goalStudyHours by remember{
        mutableStateOf("")
    }

    var selectedColor by rememberSaveable {
        mutableStateOf(Subject.subjectCardColors.random())
    }

    AddSubjectDialogue(
        isOpen = isAddSubjectDialogueOpen,
        subjectName = subjectName,
        goalStudyHours = goalStudyHours,
        selectedColors = selectedColor,
        onDismissRequest = {
            isAddSubjectDialogueOpen = false
            subjectName = ""
            goalStudyHours = ""
            selectedColor = Subject.subjectCardColors.random()
        },
        onConfirmButtonClick = {
            isAddSubjectDialogueOpen = false
            subjectName = ""
            goalStudyHours = ""
            selectedColor = Subject.subjectCardColors.random()
        },
        onColorChange = {newValue -> selectedColor = newValue},
        onSubjectNameChange = {newValue -> subjectName = newValue},
        onGoalStudyHoursChange = {newValue -> goalStudyHours = newValue}
    )

    DeleteDialogue(
        isOpen = isDeleteDialogueOpen,
        title = "Delete Session?",
        bodyText = "Are you sure you want to delete this session?\nYour studied hours will be " +
                "reduced by this session's time.\nTHIS ACTION CANNOT BE UNDONE.",
        onDismissRequest = { isDeleteDialogueOpen = false },
        onConfirmButtonClick = { isDeleteDialogueOpen = false }
    )

    Scaffold( topBar = { DashboardScreenTopBar() }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item { //count cards
                CountCardsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    subjectCount = 5,
                    studiedHours = "10",
                    goalStudyHours = "15"
                )
            }

            item{//subject cards
                SubjectCardsSection(
                    modifier = Modifier.fillMaxWidth(),
                    //subjectsList = emptyList()
                    subjectsList = subjects,
                    onAddIconClick = {isAddSubjectDialogueOpen = true}
                )
            }

            item{// start study session button
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 20.dp),
                    onClick = { /*TODO*/ }) {
                        Text(text = "Start Study Session")
                }
            }

            tasksList(
                sectionTitle = "UPCOMING TASKS",
                //tasks = emptyList(),
                tasks = tasks,
                emptyListText = "You don't have any upcoming tasks.\n Click the + button in the" +
                        " subject screen to add a new task.",
                onTaskCardClick = {},
                onCheckBoxClick = {}
            )

            item{
                Spacer(modifier = Modifier.height(20.dp))
            }

            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                //sessions = emptyList(),
                sessions = sessions,
                emptyListText = "You don't have any recent study sessions.\n Start a study " +
                        "session to begin recording your progress.\n",
                onDeleteIconClick = { isDeleteDialogueOpen = true}
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DashboardScreenTopBar(){
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Study Smart",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    )
}

@Composable
private fun CountCardsSection(
    modifier: Modifier,
    subjectCount: Int,
    studiedHours: String,
    goalStudyHours: String
){
    Row(modifier){
        CountCard(
            modifier = Modifier.weight(1f), //so that all the cards will be equally sized
            headingText = "Subject Count",
            count = "$subjectCount"
        )

        Spacer(modifier = Modifier.width(10.dp))

        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Studied Hours",
            count = studiedHours)

        Spacer(modifier = Modifier.width(10.dp))

        CountCard(modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count = goalStudyHours)
    }
}

@Composable
private fun SubjectCardsSection(
    modifier: Modifier,
    subjectsList: List<Subject>,
    emptyListText: String = "You currently don't have any subjects.\n Click the + button to add " +
            "a new subject",
    onAddIconClick: () -> Unit
){
    Column (modifier = modifier){
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = "SUBJECTS",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp)
            )

            IconButton(onClick = onAddIconClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Subject"
                )
            }
        }

        if(subjectsList.isEmpty()){
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
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

        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(12.dp),//space between each
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp) //space at the beginning and end
        ){
            items(subjectsList){subject ->
                SubjectCard(
                    subjectName = subject.subjectName,
                    gradientColors = subject.colors,
                    onClick = {})
            }
        }
    }
}