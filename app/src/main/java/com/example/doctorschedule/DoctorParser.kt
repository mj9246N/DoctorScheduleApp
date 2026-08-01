package com.example.doctorschedule

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

object DoctorParser {

    fun parseDoctors(html: String): List<Doctor> {
        val doc: Document = Jsoup.parse(html)
        val cards: List<Element> = doc.select("div.k-card")
        val doctors = mutableListOf<Doctor>()

        for (card in cards) {
            val name = card.selectFirst("h6")?.text()?.trim() ?: continue
            val specialty = card.selectFirst("small")?.text()?.trim() ?: ""
            val scheduleItems = card.select("ul.list-unstyled li")
            for (item in scheduleItems) {
                val fullText = item.text().trim()
                val regex = Regex("""^(.+?)\s*\((.+?)\)$""")
                val match = regex.find(fullText)
                if (match != null) {
                    val day = match.groupValues[1].trim()
                    val time = match.groupValues[2].trim()
                    doctors.add(Doctor(name, specialty, day, time))
                } else {
                    doctors.add(Doctor(name, specialty, fullText, ""))
                }
            }
        }
        return doctors
    }

    fun parsePageCount(html: String): Int {
        val doc = Jsoup.parse(html)
        val pagerText = doc.select("div.k-pager-wrap").text()
        val regex = Regex("""صفحه\s+\d+\s+از\s+(\d+)""")
        val match = regex.find(pagerText)
        return match?.groupValues?.get(1)?.toIntOrNull() ?: 1
    }
}
