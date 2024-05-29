package com.example.studysmartandroidapp.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.studysmartandroidapp.R
import com.example.studysmartandroidapp.presentation.components.CountCard
import com.example.studysmartandroidapp.presentation.components.SubjectCard
import com.example.studysmartandroidapp.presentation.components.tasksList
import com.example.studysmartandroidapp.presentation.domain.model.Subject

@Composable
fun DashboardScreen(){

    val subjects = listOf(
        Subject(subjectName = "English", goalStudyHours = 10f, colors = Subject.subjectCardColors[0]),
        Subject(subjectName = "Physics", goalStudyHours = 10f, colors = Subject.subjectCardColors[1]),
        Subject(subjectName = "Maths", goalStudyHours = 10f, colors = Subject.subjectCardColors[2]),
        Subject(subjectName = "Geology", goalStudyHours = 10f, colors = Subject.subjectCardColors[3]),
        Subject(subjectName = "Fine Arts", goalStudyHours = 10f, colors = Subject.subjectCardColors[4])
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
                    subjectsList = emptyList()
                    //subjectsList = subjects
                )
            }

            item{// start study session button
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal =48.dp, vertical = 20.dp),
                    onClick = { /*TODO*/ }) {
                        Text(text = "Start Study Session")
                }
            }

            tasksList(
                sectionTitle = "UPCOMING TASKS",
                tasks = emptyList(),
                emptyListText = "You don't have any upcoming tasks.\n Click the + button in the" +
                        " subject screen to add a new task."
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
            "a new subject"
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

            IconButton(onClick = { /*TODO*/ }) {
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