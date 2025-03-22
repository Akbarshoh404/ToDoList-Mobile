package com.example.todolist.model

data class UserData(
    val fullName: String,
    val email: String,
    val photoUrl: String,
    val uid: String,
    val tasks: List<Task> = emptyList()
)

data class Task(
    var id: String = "",
    var time: String = "",
    var taskName: String = "",
    var type: String = "",
    var check: Boolean = false,
    var description: String = ""
)