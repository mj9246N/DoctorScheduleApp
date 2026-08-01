package com.example.doctorschedule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DoctorRepository {
    // تاریخ‌های پیش‌فرض مطابق با سایت برای دریافت همهٔ پزشکان
    private const val BASE_URL = "https://nobatsh.abadanums.ac.ir/QueueWeb/DoctorSchedule?StartDate=1405/05/10&EndDate=1405/05/16"

    suspend fun getAllDoctors(): List<Doctor> = withContext(Dispatchers.IO) {
        val firstHtml = NetworkClient.fetchHtml(BASE_URL) ?: return@withContext emptyList()
        val totalPages = DoctorParser.parsePageCount(firstHtml)
        val allDoctors = DoctorParser.parseDoctors(firstHtml).toMutableList()

        if (totalPages > 1) {
            for (page in 2..totalPages) {
                val pageUrl = "https://nobatsh.abadanums.ac.ir/QueueWeb/DoctorSchedule?StartDate=1405/05/10&EndDate=1405/05/16&page=$page"
                val pageHtml = NetworkClient.fetchHtml(pageUrl)
                if (pageHtml != null) {
                    allDoctors.addAll(DoctorParser.parseDoctors(pageHtml))
                }
            }
        }
        allDoctors
    }
}
