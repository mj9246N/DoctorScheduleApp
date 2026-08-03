package com.example.doctorschedule

import java.util.Calendar

object PersianDateUtil {
    // روزهای هفته شمسی
    private val weekDays = arrayOf("شنبه","یکشنبه","دوشنبه","سه‌شنبه","چهارشنبه","پنج‌شنبه","جمعه")
    // ماه‌های شمسی
    private val months = arrayOf("فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند")

    fun getTodayShamsi(): String {
        val cal = Calendar.getInstance()
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)
        val jy = gy - 621
        val jm: Int
        val jd: Int
        var days = 0
        val gDaysInMonth = intArrayOf(0,31, if (isLeap(gy)) 29 else 28,31,30,31,30,31,31,30,31,30,31)
        for (i in 1 until gm) days += gDaysInMonth[i]
        days += gd
        if (days <= 79) {
            jy--
            days += if (isLeap(gy-1)) 10 else 9
        }
        val jDaysInMonth = intArrayOf(0,31,31,31,31,31,31,30,30,30,30,30,29)
        var jmIndex = 1
        while (days > jDaysInMonth[jmIndex]) {
            days -= jDaysInMonth[jmIndex]
            jmIndex++
        }
        val jmFinal = jmIndex
        val jdFinal = days
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        // تبدیل روز هفته میلادی به شمسی (جمعه = 7)
        val shamsiDayIndex = if (dayOfWeek == Calendar.SATURDAY) 0
                        else if (dayOfWeek == Calendar.SUNDAY) 1
                        else if (dayOfWeek == Calendar.MONDAY) 2
                        else if (dayOfWeek == Calendar.TUESDAY) 3
                        else if (dayOfWeek == Calendar.WEDNESDAY) 4
                        else if (dayOfWeek == Calendar.THURSDAY) 5
                        else 6
        return "${weekDays[shamsiDayIndex]} $jdFinal ${months[jmFinal-1]}"
    }

    private fun isLeap(year: Int) = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
}
