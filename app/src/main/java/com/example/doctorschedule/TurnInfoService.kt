package com.example.doctorschedule

import org.json.JSONObject

object TurnInfoService {
    private const val BASE_URL = "https://nobatsh.abadanums.ac.ir/QueueWeb/DoctorSchedule/GetTurnInfo"

    data class TurnInfo(
        val appoint: Int,
        val telAppoint: Int,
        val webAppoint: Int
    )

    suspend fun getTurnInfo(dateShowId: Int): TurnInfo? {
        val url = "$BASE_URL?dateShowId=$dateShowId"
        val json = NetworkClient.fetchHtml(url) ?: return null
        return try {
            val obj = JSONObject(json)
            TurnInfo(
                appoint = obj.optInt("appoint", 0),
                telAppoint = obj.optInt("telAppoint", 0),
                webAppoint = obj.optInt("webAppoint", 0)
            )
        } catch (e: Exception) {
            null
        }
    }
}
