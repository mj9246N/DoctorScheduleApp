package com.example.doctorschedule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DoctorRepository {
    private const val BASE_URL = "https://nobatsh.abadanums.ac.ir/QueueWeb/DoctorSchedule"

    suspend fun getAllDoctors(): List<Doctor> = withContext(Dispatchers.IO) {
        val firstHtml = NetworkClient.fetchHtml(BASE_URL) ?: return@withContext emptyList()
        val totalPages = DoctorParser.parsePageCount(firstHtml)
        val allDoctors = DoctorParser.parseDoctors(firstHtml).toMutableList()

        if (totalPages > 1) {
            for (page in 2..totalPages) {
                val pageHtml = NetworkClient.fetchHtml("$BASE_URL?page=$page")
                if (pageHtml != null) {
                    allDoctors.addAll(DoctorParser.parseDoctors(pageHtml))
                }
            }
        }
        allDoctors
    }
}
