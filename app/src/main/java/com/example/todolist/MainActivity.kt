package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                            route = "taskDetail/{check}/{taskName}/{time}/{type}/{description}/{taskId}",
                            arguments = listOf(
                                navArgument("check") { type = NavType.BoolType },
                                navArgument("taskName") { type = NavType.StringType },
                                navArgument("time") { type = NavType.StringType },
                                navArgument("type") { type = NavType.StringType },
                                navArgument("description") { type = NavType.StringType },
                                navArgument("taskId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            TaskDetailScreen(
                                navController = navController,
                                check = backStackEntry.arguments?.getBoolean("check") ?: false,
                                taskName = backStackEntry.arguments?.getString("taskName") ?: "",
                                time = backStackEntry.arguments?.getString("time") ?: "",
                                type = backStackEntry.arguments?.getString("type") ?: "",
                                description = backStackEntry.arguments?.getString("description") ?: "",
                                taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                            )
                        }
                    }
                }
            }
        }
    }
}