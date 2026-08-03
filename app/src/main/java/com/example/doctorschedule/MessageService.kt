package com.example.doctorschedule

import org.json.JSONArray

object MessageService {
    fun fetchMessages(callback: (List<String>) -> Unit) {
        val workerUrl = BuildConfig.WORKER_URL
        if (workerUrl.isBlank()) {
            callback(emptyList())
            return
        }
        Thread {
            try {
                val html = NetworkClient.fetchHtml("${workerUrl}/messages")
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
