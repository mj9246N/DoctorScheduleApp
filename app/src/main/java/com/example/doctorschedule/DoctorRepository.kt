package com.example.doctorschedule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DoctorRepository {
    // بدون پارامتر تاریخ؛ سایت خودش هفته جاری را برمی‌گرداند
    private const val BASE_URL = "https://nobatsh.abadanums.ac.ir/QueueWeb/DoctorSchedule"

    suspend fun getAllDoctors(): List<Doctor> = withContext(Dispatchers.IO) {
        val firstHtml = NetworkClient.fetchHtml(BASE_URL) ?: return@withContext emptyList()
        val totalPages = DoctorParser.parsePageCount(firstHtml)
        val allDoctors = DoctorParser.parseDoctors(firstHtml).toMutableList()

        if (totalPages > 1) {
            for (page in 2..totalPages) {
                val pageUrl = "$BASE_URL?page=$page"
                val pageHtml = NetworkClient.fetchHtml(pageUrl)
                if (pageHtml != null) {
                    allDoctors.addAll(DoctorParser.parseDoctors(pageHtml))
                }
            }
        }
        allDoctors
    }

    suspend fun getDateRange(): DoctorParser.DateRange = withContext(Dispatchers.IO) {
        val html = NetworkClient.fetchHtml(BASE_URL) ?: return@withContext DoctorParser.DateRange("", "")
        DoctorParser.parseDateRange(html)
    }
}
