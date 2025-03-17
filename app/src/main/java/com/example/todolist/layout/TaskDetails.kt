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
    taskIndex: Int = -1 // Add taskIndex as an optional parameter to update Firebase
) {
    // Decode the URL-encoded strings
    val decodedTaskName = URLDecoder.decode(taskName, "UTF-8")
    val decodedTime = URLDecoder.decode(time, "UTF-8")
    val decodedType = URLDecoder.decode(type, "UTF-8")
    val decodedDescription = URLDecoder.decode(description, "UTF-8")

    // Local state for check status
    var isChecked by remember { mutableStateOf(check) }

    // Firebase reference
    val userId = "NSb9DNcxoSgX3LcDpkdtNXBp8CA2" // Match with HomeScreen
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
                    if (taskIndex != -1) {
                        databaseRef.child(taskIndex.toString()).child("check").setValue(isChecked)
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

            // Delete Button
            Button(
                onClick = {
                    if (taskIndex != -1) {
                        databaseRef.child(taskIndex.toString()).removeValue()
                            .addOnSuccessListener {
                                navController.popBackStack()
                            }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), // Red color for delete
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Text(text = "Delete Task", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Back Button
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