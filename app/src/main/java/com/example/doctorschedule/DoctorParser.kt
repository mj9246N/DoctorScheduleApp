package com.example.doctorschedule

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

object DoctorParser {

    fun parseDoctors(html: String): List<Doctor> {
        val doc: Document = Jsoup.parse(html)
        val cards: List<Element> = doc.select("div.card-deck div.card")

        val grouped = linkedMapOf<String, MutableList<Schedule>>()
        val specialtyMap = mutableMapOf<String, String>()
        val clinicMap = mutableMapOf<String, String>()

        for (card in cards) {
            val name = card.selectFirst("div.card-body div.card-title h6")?.text()?.trim() ?: continue
            val specialty = card.selectFirst("div.card-body div.card-title small")?.text()?.trim() ?: ""
            val clinic = card.selectFirst("div.card-header small")?.text()?.trim() ?: ""

            if (!specialtyMap.containsKey(name)) specialtyMap[name] = specialty
            if (!clinicMap.containsKey(name)) clinicMap[name] = clinic

            val scheduleItems = card.select("div.card-body div.mt-2 ul.list-unstyled li a")
            val schedules = grouped.getOrPut(name) { mutableListOf() }

            for (aTag in scheduleItems) {
                val smallBlock = aTag.selectFirst("small.d-block") ?: continue
                val fullText = smallBlock.text().trim()
                val onclick = aTag.attr("onclick") ?: ""

                // استخراج showId
                var showId = -1
                val idRegex = Regex("""getTurnInfo\((\d+)""")
                val idMatch = idRegex.find(onclick)
                if (idMatch != null) {
                    showId = idMatch.groupValues[1].toIntOrNull() ?: -1
                }

                // جدا کردن روز و ساعت با آخرین پرانتز
                val lastOpenParen = fullText.lastIndexOf('(')
                if (lastOpenParen >= 0 && fullText.endsWith(')')) {
                    val day = fullText.substring(0, lastOpenParen).trim()
                    val time = fullText.substring(lastOpenParen + 1, fullText.length - 1).trim()
                    schedules.add(Schedule(showId, day, time))
                } else {
                    // فرمت غیرمنتظره: کل متن را روز در نظر می‌گیریم
                    schedules.add(Schedule(showId, fullText, ""))
                }
            }
        }

        return grouped.map { (name, schedules) ->
            Doctor(
                name = name,
                specialty = specialtyMap[name] ?: "",
                clinic = clinicMap[name] ?: "",
                schedules = schedules
            )
        }
    }

    fun parsePageCount(html: String): Int {
        val doc = Jsoup.parse(html)
        val pagingText = doc.select("div.paging").text()
        val regex = Regex("""(\d+)\s*از\s*(\d+)""")
        val match = regex.find(pagingText)
        return match?.groupValues?.get(2)?.toIntOrNull() ?: 1
    }
}
