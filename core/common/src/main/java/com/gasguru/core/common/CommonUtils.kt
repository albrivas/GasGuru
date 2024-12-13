package com.gasguru.core.common

import com.gasguru.core.model.data.FuelStation
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object CommonUtils {

    fun getAppVersion(): String {
        val versionName =
            "${BuildConfig.versionMajor}.${BuildConfig.versionMinor}.${BuildConfig.versionPatch}"
        return "$versionName (${BuildConfig.versionCode})"
    }

    @Suppress("ReturnCount")
    fun FuelStation.isStationOpen(): Boolean {
        val now = java.time.ZonedDateTime.now()
        val currentDay = now.dayOfWeek
        val currentTime = now.toLocalTime()

        if (schedule.trim().uppercase(java.util.Locale.ROOT) == "L-D: 24H") {
            return true
        }

        val scheduleParts = schedule.split(";")
        for (part in scheduleParts) {
            val regex = Regex("""([LMXJVSD-]+):\s*([0-9]{2}:[0-9]{2})-([0-9]{2}:[0-9]{2})""")
            val matchResult = regex.find(part.trim())

            if (matchResult != null) {
                val days = matchResult.groupValues[1]
                val startTime = matchResult.groupValues[2]
                val endTime = matchResult.groupValues[3]

                if (isDayMatched(days, currentDay) && isTimeInRange(startTime, endTime, currentTime)) {
                    return true
                }
            }
        }

        return false
    }

    private fun isTimeInRange(startTimeStr: String, endTimeStr: String, currentTime: LocalTime): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        val startTime = LocalTime.parse(startTimeStr, formatter)
        val endTime = LocalTime.parse(endTimeStr, formatter)

        if (endTime.isAfter(startTime) || endTime == startTime) {
            return currentTime.isAfter(startTime) && currentTime.isBefore(endTime)
        }

        return currentTime.isAfter(startTime) || currentTime.isBefore(endTime)
    }

    private fun isDayMatched(days: String, currentDay: DayOfWeek): Boolean {
        return when (days) {
            "L-D" -> true
            "L-V" -> currentDay.value in 1..5
            "L-S" -> currentDay.value in 1..6
            "L" -> currentDay == DayOfWeek.MONDAY
            "M" -> currentDay == DayOfWeek.TUESDAY
            "X" -> currentDay == DayOfWeek.WEDNESDAY
            "J" -> currentDay == DayOfWeek.THURSDAY
            "V" -> currentDay == DayOfWeek.FRIDAY
            "S" -> currentDay == DayOfWeek.SATURDAY
            "D" -> currentDay == DayOfWeek.SUNDAY
            else -> false
        }
    }
}
