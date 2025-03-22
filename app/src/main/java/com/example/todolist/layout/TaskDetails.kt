package com.example.todolist.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.net.URLDecoder

@Composable
fun TaskDetailScreen(
    navController: NavController,
    check: Boolean,
    taskName: String,
    time: String,
    type: String,
    description: String,
    taskId: String
) {
    val decodedTaskName = URLDecoder.decode(taskName, "UTF-8")
    val decodedTime = URLDecoder.decode(time, "UTF-8")
    val decodedType = URLDecoder.decode(type, "UTF-8")
    val decodedDescription = URLDecoder.decode(description, "UTF-8")
    val decodedTaskId = URLDecoder.decode(taskId, "UTF-8")

    var isChecked by remember { mutableStateOf(check) }

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid ?: run {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/tasks")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F3EA)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = decodedTaskName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status: ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = if (isChecked) "Completed" else "Pending",
                    fontSize = 16.sp,
                    color = if (isChecked) Color(0xFF4CAF50) else Color(0xFFFF9800),
                    fontStyle = FontStyle.Italic
                )
            }

            Text(
                text = "Type: $decodedType",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Time: $decodedTime",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Description:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            Text(
                text = decodedDescription.ifEmpty { "No description provided" },
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Toggle Status Button
            Button(
                onClick = {
                    isChecked = !isChecked
                    if (decodedTaskId.isNotEmpty()) {
                        databaseRef.child(decodedTaskId).child("check").setValue(isChecked)
                            .addOnSuccessListener { println("Status updated successfully") }
                            .addOnFailureListener { e -> println("Failed to update status: ${e.message}") }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = if (isChecked) "Mark as Pending" else "Mark as Completed",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (decodedTaskId.isNotEmpty()) {
                        databaseRef.child(decodedTaskId).removeValue()
                            .addOnSuccessListener {
                                println("Task deleted successfully with ID: $decodedTaskId")
                                navController.popBackStack()
                            }
                            .addOnFailureListener { exception ->
                                println("Failed to delete task: ${exception.message}")
                            }
                    } else {
                        println("Invalid task ID: $decodedTaskId")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Text(text = "Delete Task", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Text(text = "Back", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}