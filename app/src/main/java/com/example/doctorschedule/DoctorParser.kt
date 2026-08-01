package com.example.doctorschedule

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

object DoctorParser {

    fun parseDoctors(html: String): List<Doctor> {
        val doc: Document = Jsoup.parse(html)
        val cards: List<Element> = doc.select("div.card-deck div.card")
        val doctors = mutableListOf<Doctor>()

        for (card in cards) {
            val name = card.selectFirst("div.card-body div.card-title h6")?.text()?.trim() ?: continue
            val specialty = card.selectFirst("div.card-body div.card-title small")?.text()?.trim() ?: ""
            val scheduleItems = card.select("div.card-body div.mt-2 ul.list-unstyled li a small.d-block")
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
        // استخراج از «1 از 2» درون span های badge
        val pagerText = doc.select("div.paging span").text()
        val regex = Regex("""(\d+)\s+از\s+(\d+)""")
        val match = regex.find(pagerText)
        return match?.groupValues?.get(2)?.toIntOrNull() ?: 1
    }
}
