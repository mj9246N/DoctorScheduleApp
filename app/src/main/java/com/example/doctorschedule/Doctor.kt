package com.example.doctorschedule

data class Doctor(
    val name: String,
    val specialty: String,
    val clinic: String,
    val schedules: List<Schedule>
)

data class Schedule(
    val showId: Int,
    val day: String,
    val time: String
)
