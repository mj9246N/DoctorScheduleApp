package com.example.doctorschedule

object PersianDateUtil {
    private val weekDays = arrayOf("شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه")
    private val months = arrayOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند")
    private val monthDays = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29) // ۱۴۰۵ عادی است

    /**
     * با گرفتن تاریخ شروع شمسی (مثلاً 1405/05/11 که همیشه شنبه است)،
     * تاریخ امروز را به صورت «شنبه 11 مرداد» برمی‌گرداند.
     */
    fun getTodayShamsi(startDateShamsi: String): String {
        val parts = startDateShamsi.split("/").map { it.toInt() }
        var year = parts[0]
        var month = parts[1]
        var day = parts[2]

        // چند روز از شنبه گذشته؟
        val todayCal = java.util.Calendar.getInstance()
        val daysFromSaturday = when (todayCal.get(java.util.Calendar.DAY_OF_WEEK)) {
            java.util.Calendar.SATURDAY -> 0
            java.util.Calendar.SUNDAY -> 1
            java.util.Calendar.MONDAY -> 2
            java.util.Calendar.TUESDAY -> 3
            java.util.Calendar.WEDNESDAY -> 4
            java.util.Calendar.THURSDAY -> 5
            else -> 6 // جمعه
        }

        // اضافه کردن فاصله به تاریخ شروع
        day += daysFromSaturday
        while (day > monthDays[month - 1]) {
            day -= monthDays[month - 1]
            month++
            if (month > 12) {
                month = 1
                year++
            }
        }

        val monthName = months[month - 1]
        val weekDayIndex = daysFromSaturday % 7
        return "${weekDays[weekDayIndex]} $day $monthName"
    }
}
