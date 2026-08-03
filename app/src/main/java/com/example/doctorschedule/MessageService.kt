package com.example.doctorschedule

import org.json.JSONArray
import org.json.JSONObject

object MessageService {
    // آدرس Worker خود را جایگزین کنید
    private const val WORKER_URL = "https://your-worker.your-subdomain.workers.dev"

    fun fetchMessages(callback: (List<String>) -> Unit) {
        Thread {
            try {
                val html = NetworkClient.fetchHtml("$WORKER_URL/messages")
                if (html != null) {
                    val jsonArray = JSONArray(html)
                    val list = mutableListOf<String>()
                    for (i in 0 until jsonArray.length()) {
                        list.add(jsonArray.getString(i))
                    }
                    callback(list)
                } else {
                    callback(emptyList())
                }
            } catch (e: Exception) {
                callback(emptyList())
            }
        }.start()
    }
}
