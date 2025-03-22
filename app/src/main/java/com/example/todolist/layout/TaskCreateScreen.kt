package com.example.todolist.layout

import android.app.TimePickerDialog
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

@Composable
fun TaskCreateScreen(navController: NavHostController) {
    var taskName by remember { mutableStateOf("") }
    var taskType by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("⌛Select Time") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val timePickerDialog = TimePickerDialog(
        context,
        { _: TimePicker, hour: Int, minute: Int ->
            selectedTime = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    // Get the authenticated user's ID
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
        LaunchedEffect(Unit) {
            errorMessage = "You must be logged in to create a task"
            navController.navigate("login") // Redirect to login if not authenticated
        }
        return
    }
    val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/tasks")

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            errorMessage = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F3EA)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Task",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            InputField(
                value = taskName,
                onValueChange = { taskName = it },
                placeholder = "Task Name"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InputField(
                value = taskType,
                onValueChange = { taskType = it },
                placeholder = "Task Type"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(12.dp)
            ) {
                if (taskDescription.isEmpty()) {
                    Text(
                        text = "Description",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                BasicTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    singleLine = false,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { timePickerDialog.show() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(12.dp)
            ) {
                Text(
                    text = selectedTime,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    println("Create button clicked")
                    when {
                        taskName.isBlank() -> errorMessage = "Task Name is required"
                        taskType.isBlank() -> errorMessage = "Task Type is required"
                        selectedTime == "⌛Select Time" -> errorMessage = "Please select a time"
                        else -> {
                            val newTask = Task(
                                check = false,
                                taskName = taskName,
                                time = selectedTime,
                                type = taskType,
                                description = taskDescription
                            )
                            val newTaskRef = databaseRef.push()
                            newTask.id = newTaskRef.key ?: ""
                            println("Saving task with ID: ${newTask.id} for user: $userId")

                            newTaskRef.setValue(newTask)
                                .addOnSuccessListener {
                                    println("Task saved successfully")
                                    errorMessage = "Task created successfully"
                                    taskName = ""
                                    taskType = ""
                                    taskDescription = ""
                                    selectedTime = "⌛Select Time"
                                    navController.navigate("home")
                                }
                                .addOnFailureListener { e ->
                                    println("Failed to create task: ${e.message}")
                                    errorMessage = "Failed to create task: ${e.message}"
                                }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Text(text = "Create", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(5.dp))

            Button(
                onClick = { navController.navigate("home") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Text(text = "Cancel", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun InputField(value: String, onValueChange: (String) -> Unit, placeholder: String, isMultiline: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            singleLine = !isMultiline,
            modifier = Modifier.fillMaxWidth()
        )
    }
}