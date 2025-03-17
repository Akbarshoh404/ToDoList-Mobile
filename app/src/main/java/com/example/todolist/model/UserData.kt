package com.example.todolist.model

data class UserData(
    val fullName: String,
    val email: String,
    val photoUrl: String,
    val uid: String,
    val tasks: List<Task> = emptyList() // Default to an empty list
)

data class Task(
    var time: String,
    var taskName: String,
    var type: String,
    var check: Boolean,
    var description: String
)
