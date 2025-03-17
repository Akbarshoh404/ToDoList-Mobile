package com.example.todolist.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder

data class Task(
    var check: Boolean = false,
    val taskName: String = "",
    val time: String = "",
    val type: String = "",
    val description: String = ""
)

@Composable
fun HomeScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf("All") }
    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var taskTypes by remember { mutableStateOf(listOf<String>()) }
    val coroutineScope = rememberCoroutineScope()

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid ?: return

    val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/tasks")

    LaunchedEffect(Unit) {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskList = mutableListOf<Task>()
                val typesSet = mutableSetOf<String>()

                snapshot.children.forEachIndexed { index, taskSnapshot ->
                    val task = taskSnapshot.getValue(Task::class.java)
                    task?.let {
                        taskList.add(it)
                        typesSet.add(it.type)
                    }
                }

                tasks = taskList
                taskTypes = typesSet.toList()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase Error: ${error.message}")
            }
        })
    }

    val filteredTasks = remember(searchQuery, selectedSort, tasks) {
        tasks.filter {
            when (selectedSort) {
                "All" -> !it.check &&
                        (searchQuery.isBlank() || it.taskName.contains(searchQuery, ignoreCase = true))
                "Done" -> it.check &&
                        (searchQuery.isBlank() || it.taskName.contains(searchQuery, ignoreCase = true))
                else -> it.type == selectedSort &&
                        (searchQuery.isBlank() || it.taskName.contains(searchQuery, ignoreCase = true))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F3EA))
            .padding(16.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black, fontWeight = FontWeight.Bold)) {
                    append("Todo")
                }
                withStyle(style = SpanStyle(color = Color(0xFF8DE0C8), fontWeight = FontWeight.Bold)) {
                    append("List")
                }
            },
            fontSize = 32.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(12.dp),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search...",
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { navController.navigate("taskCreate") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            ) {
                Text(text = "+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("All", "Done")) { option ->
                Button(
                    onClick = { selectedSort = option },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedSort == option) Color.Black else Color.Gray
                    ),
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Text(text = option, color = Color.White)
                }
            }

            items(taskTypes) { type ->
                Button(
                    onClick = { selectedSort = type },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedSort == type) Color.Black else Color.Gray
                    ),
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Text(text = type, color = Color.White)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(filteredTasks, key = { it.taskName + it.time }) { task ->
                var isVisible by remember { mutableStateOf(!task.check) }

                AnimatedVisibility(
                    visible = isVisible,
                    exit = fadeOut(animationSpec = tween(durationMillis = 500)) +
                            slideOutVertically(
                                animationSpec = tween(durationMillis = 500),
                                targetOffsetY = { -it } // Slide up
                            )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val taskIndex = tasks.indexOf(task)
                                val encodedTaskName = URLEncoder.encode(task.taskName, "UTF-8")
                                val encodedTime = URLEncoder.encode(task.time, "UTF-8")
                                val encodedType = URLEncoder.encode(task.type, "UTF-8")
                                val encodedDescription = URLEncoder.encode(task.description, "UTF-8")
                                val route = "taskDetail/${task.check}/$encodedTaskName/$encodedTime/$encodedType/$encodedDescription/$taskIndex"
                                navController.navigate(route)
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.taskName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Text(
                                    text = task.time,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                            Checkbox(
                                checked = task.check,
                                onCheckedChange = { newValue ->
                                    val taskIndex = tasks.indexOf(task)
                                    if (taskIndex != -1) {
                                        if (newValue) {
                                            coroutineScope.launch {
                                                databaseRef.child(taskIndex.toString())
                                                    .child("check")
                                                    .setValue(true)
                                                delay(500)
                                                isVisible = false
                                            }
                                        } else {
                                            databaseRef.child(taskIndex.toString())
                                                .child("check")
                                                .setValue(false)
                                            isVisible = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}