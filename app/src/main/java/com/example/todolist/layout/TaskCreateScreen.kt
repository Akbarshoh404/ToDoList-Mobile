package com.example.todolist.layout

import android.app.TimePickerDialog
import android.widget.TimePicker
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
import com.google.firebase.database.FirebaseDatabase
import java.util.*

@Composable
fun TaskCreateScreen(navController: NavHostController) {
    var taskName by remember { mutableStateOf("") }
    var taskType by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("⌛Select Time") }

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

    // Firebase setup
    val userId = "NSb9DNcxoSgX3LcDpkdtNXBp8CA2" // Should match HomeScreen
    val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/tasks")

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
                    // Create new task and save to Firebase
                    if (taskName.isNotBlank() && taskType.isNotBlank() && selectedTime != "⌛Select Time") {
                        val newTask = Task(
                            check = false,
                            taskName = taskName,
                            time = selectedTime,
                            type = taskType,
                            description = taskDescription
                        )

                        // Push new task to Firebase (creates new node with auto-generated ID)
                        databaseRef.push().setValue(newTask)
                            .addOnSuccessListener {
                                // Clear fields and navigate back on success
                                taskName = ""
                                taskType = ""
                                taskDescription = ""
                                selectedTime = "⌛Select Time"
                                navController.navigate("home")
                            }
                            .addOnFailureListener { e ->
                                // Handle error (could add a snackbar/toast here)
                                println("Failed to create task: ${e.message}")
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