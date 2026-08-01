package com.example.doctorschedule

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

object DoctorParser {

    fun parseDoctors(html: String): List<Doctor> {
        val doc: Document = Jsoup.parse(html)
        val cards: List<Element> = doc.select("div.card-deck div.card")
        
        // استفاده از LinkedHashMap برای حفظ ترتیب و گروه‌بندی
        val grouped = linkedMapOf<String, MutableList<String>>()
        val specialtyMap = mutableMapOf<String, String>()

        for (card in cards) {
            val name = card.selectFirst("div.card-body div.card-title h6")?.text()?.trim() ?: continue
            val specialty = card.selectFirst("div.card-body div.card-title small")?.text()?.trim() ?: ""
            val scheduleItems = card.select("div.card-body div.mt-2 ul.list-unstyled li a small.d-block")
            
            // ذخیره تخصص برای هر نام (اگر قبلاً نبود)
            if (!specialtyMap.containsKey(name)) {
                specialtyMap[name] = specialty
            }
            
            val schedules = grouped.getOrPut(name) { mutableListOf() }
            for (item in scheduleItems) {
                val fullText = item.text().trim()
                schedules.add(fullText)   // دقیقاً همان فرمت «روز (ساعت)» را اضافه کن
            }
        }

        // تبدیل به لیست Doctor
        return grouped.map { (name, schedules) ->
            Doctor(
                name = name,
                specialty = specialtyMap[name] ?: "",
                schedules = schedules
            )
        }
    }

    fun parsePageCount(html: String): Int {
        val doc = Jsoup.parse(html)
        // کل متن داخل div.paging را بگیر (مثلاً «صفحه 1 از 2»)
        val pagingText = doc.select("div.paging").text()
        // استخراج دو عدد: شماره صفحه فعلی و تعداد کل صفحات
        val regex = Regex("""(\d+)\s*از\s*(\d+)""")
        val match = regex.find(pagingText)
        // عدد دوم تعداد کل صفحات است
        return match?.groupValues?.get(2)?.toIntOrNull() ?: 1
    }
}
