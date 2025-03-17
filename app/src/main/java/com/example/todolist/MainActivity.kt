package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolist.layout.HomeScreen
import com.example.todolist.layout.LoginScreen
import com.example.todolist.layout.TaskCreateScreen
import com.example.todolist.layout.TaskDetailScreen
import com.example.todolist.ui.theme.ToDoListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginScreen(navController)
                        }
                        composable("home") {
                            HomeScreen(navController)
                        }
                        composable("taskCreate") {
                            TaskCreateScreen(navController)
                        }
                        composable(
                            route = "taskDetail/{check}/{taskName}/{time}/{type}/{description}/{taskIndex}"
                        ) { backStackEntry ->
                            val check = backStackEntry.arguments?.getString("check")?.toBoolean() ?: false
                            val taskName = backStackEntry.arguments?.getString("taskName") ?: ""
                            val time = backStackEntry.arguments?.getString("time") ?: ""
                            val type = backStackEntry.arguments?.getString("type") ?: ""
                            val description = backStackEntry.arguments?.getString("description") ?: ""
                            val taskIndex = backStackEntry.arguments?.getString("taskIndex")?.toIntOrNull() ?: -1
                            TaskDetailScreen(
                                navController = navController,
                                check = check,
                                taskName = taskName,
                                time = time,
                                type = type,
                                description = description,
                                taskIndex = taskIndex
                            )
                        }
                    }
                }
            }
        }
    }
}