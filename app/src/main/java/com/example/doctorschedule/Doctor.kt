package com.example.doctorschedule

data class Doctor(
    val name: String,
    val specialty: String,
    val schedules: List<String>   // هر عضو مثل «چهارشنبه 14 مرداد (08:00-09:30)»
)
